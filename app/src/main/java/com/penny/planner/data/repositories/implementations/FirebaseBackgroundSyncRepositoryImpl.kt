package com.penny.planner.data.repositories.implementations

import android.content.Context
import android.util.Log
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.penny.planner.data.db.budget.BudgetEntity
import com.penny.planner.data.db.category.CategoryEntity
import com.penny.planner.data.db.expense.ExpenseEntity
import com.penny.planner.data.db.groups.GroupDao
import com.penny.planner.data.db.groups.GroupEntity
import com.penny.planner.data.db.monthlyexpenses.MonthlyExpenseEntity
import com.penny.planner.data.db.subcategory.SubCategoryEntity
import com.penny.planner.data.repositories.interfaces.BudgetRepository
import com.penny.planner.data.repositories.interfaces.CategoryAndEmojiRepository
import com.penny.planner.data.repositories.interfaces.ExpenseRepository
import com.penny.planner.data.repositories.interfaces.FirebaseBackgroundSyncRepository
import com.penny.planner.data.repositories.interfaces.FriendsDirectoryRepository
import com.penny.planner.data.repositories.interfaces.MonthlyExpenseRepository
import com.penny.planner.data.repositories.interfaces.ProfilePictureRepository
import com.penny.planner.data.workmanager.ImageDownloadWorker
import com.penny.planner.helpers.Utils
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FirebaseBackgroundSyncRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val groupDao: GroupDao,
    private val budgetRepository: BudgetRepository,
    private val expenseRepository: ExpenseRepository,
    private val categoryAndEmojiRepository: CategoryAndEmojiRepository,
    private val usersRepository: FriendsDirectoryRepository,
    private val monthlyExpenseRepository: MonthlyExpenseRepository,
    private val profilePictureRepository: ProfilePictureRepository
) : FirebaseBackgroundSyncRepository {

    private val tag = "FirebaseBackgroundSyncRepositoryImpl"

    val scope = CoroutineScope(Job() + Dispatchers.IO)
    private val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    private val groupExpenseCollectionRef = db.collection(Utils.GROUP_EXPENSES)
    private val personalExpenseCollectionRef = db.collection(Utils.USER_EXPENSES)
    private val userDirectory = FirebaseDatabase.getInstance().getReference(Utils.USERS)
    private val budgetMap = mutableMapOf<String, MutableSet<String>>()
    private val subcategories = mutableMapOf<String, MutableSet<String>>()

    override fun getAllPersonalExpenses() {
        if (auth.currentUser != null) {
            scope.launch {
                fetchPersonalBudgetAndUpdate()
                fetchPersonalExpenseAndUpdate()
            }
        }
    }

    private suspend fun fetchPersonalBudgetAndUpdate() {
        val budgetDetails = personalExpenseCollectionRef.document(auth.currentUser!!.uid)
            .collection(Utils.BUDGET_DETAILS)
            .get()
            .await()
        val budgets = budgetDetails.documents.mapNotNull { document ->
            val budgetEntity = document.toObject(BudgetEntity::class.java)
            if (budgetEntity != null) {
                budgetEntity.uploadedOnServer = true
                categoryAndEmojiRepository.addCategory(
                    CategoryEntity(
                        name = budgetEntity.category,
                        icon = budgetEntity.icon
                    )
                )
            }
            budgetEntity
        }
        budgetRepository.insertBudgetListFromServer(budgets)
    }

    private suspend fun fetchPersonalExpenseAndUpdate() {
        try {
            val expensesQuery = personalExpenseCollectionRef.document(auth.currentUser!!.uid)
                .collection(Utils.EXPENSES)
                .get()
                .await()
            val allExpenses = expensesQuery.documents.mapNotNull { document ->
                val expenseEntity = document.toObject(ExpenseEntity::class.java)
                expenseEntity?.uploadedOnServer = true
                expenseEntity?.isSentTransaction = auth.currentUser?.uid == expenseEntity?.expensorId
                if (expenseEntity != null)
                    getAndAddSubCategoryFromFirebaseExpense(expenseEntity)
                expenseEntity
            }
            expenseRepository.insertBulkExpenseFromServer(allExpenses)
        } catch (e: Exception) {
            Log.d("$tag ::", "personalExpense :: $e")
        }
    }

    override fun getAllGroupsFromFirebase() {
        if (auth.currentUser != null) {
            scope.launch {
                fetchAllGroupsFromFirebase(Utils.JOINED)
                fetchAllGroupsFromFirebase(Utils.PENDING)
            }
        }
    }

    override fun getAllPendingGroups() {
        if (auth.currentUser != null) {
            scope.launch {
                fetchAllGroupsFromFirebase(Utils.PENDING)
            }
        }
    }

    override fun updateGroupsTransactions() {
        if (auth.currentUser != null) {
            scope.launch(Dispatchers.IO) {
                generateLocalMap()
                val list = groupDao.getAllExistingGroupsFromDb()
                for (group in list) {
                    fetchDataAndUpdate(group.groupId)
                }
            }
        }
    }

    override fun addGroupForFirebaseListener(groupId: String) {
        scope.launch(Dispatchers.IO) {
            fetchDataAndUpdate(groupId)
        }
    }

    override suspend fun newSubCategoryAdded(entity: SubCategoryEntity) {
        if (subcategories.containsKey(entity.category)) {
            subcategories[entity.category]!!.add(entity.name)
        } else {
            subcategories[entity.category] = mutableSetOf(entity.name)
        }
    }

    private fun fetchAllGroupsFromFirebase(source: String) {
        userDirectory
            .child(Utils.formatEmailForFirebase(auth.currentUser!!.email!!))
            .child(Utils.GROUP_INFO)
            .child(source).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val pendingGroups = snapshot.value as Map<*, *>
                        if (pendingGroups.isNotEmpty()) {
                            for (groupId in pendingGroups.keys) {
                                updateLocalWithPendingGroups(groupId.toString(), source == Utils.PENDING)
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("GroupRepository:: ", error.message)
                }
            })
    }

    private fun updateLocalWithPendingGroups(groupId: String, needUpdatePendingList: Boolean) {
        FirebaseDatabase.getInstance()
            .getReference(Utils.GROUPS)
            .child(groupId).child(Utils.GROUP_INFO).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val entity = snapshot.getValue(GroupEntity::class.java)
                        if (entity != null && entity.members.isNotEmpty()) {
                            scope.launch {
                                downloadGroupImage(entity)
                                groupDao.addGroup(entity)
                                if (monthlyExpenseRepository.getMonthlyExpenseEntity(entityId = groupId, month = Utils.getCurrentMonthYear()) == null) {
                                    monthlyExpenseRepository.addMonthlyExpenseEntity(
                                        MonthlyExpenseEntity(
                                            entityID = groupId,
                                            month = Utils.getCurrentMonthYear(),
                                            expense = 0.0
                                        )
                                    )
                                }
                                updateFriendsDb(entity.members)
                                fetchDataAndUpdate(entity.groupId)
                                if (needUpdatePendingList)
                                    updatePendingNode(entity)
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("$tag :: ", error.message)
                }
            })
    }

    private fun updateFriendsDb(list: List<String>) {
        scope.launch {
            for (email in list) {
                if (!usersRepository.doesFriendExists(email)) {
                    val userResult = usersRepository.findUserFromServer(email)
                    if (userResult.isSuccess) {
                        val friend = userResult.getOrNull()!!
                        usersRepository.addFriend(friend)
                        profilePictureRepository.downloadProfilePicture(friend)
                    }
                }
            }
        }
    }

    private fun updatePendingNode(entity: GroupEntity) {
//        userDirectory
//            .child(Utils.formatEmailForFirebase(auth.currentUser!!.email!!))
//            .child(Utils.GROUP_INFO)
//            .child(Utils.JOINED).child(entity.groupId).setValue(Utils.NON_ADMIN_VALUE)
//        userDirectory
//            .child(Utils.formatEmailForFirebase(auth.currentUser!!.email!!))
//            .child(Utils.GROUP_INFO)
//            .child(Utils.PENDING).child(entity.groupId).removeValue()
    }

    private suspend fun generateLocalMap() {
        val budgets = budgetRepository.getAllBudgets()
        for (budget in budgets) {
            if (budgetMap.containsKey(budget.entityId)) {
                budgetMap[budget.entityId]!!.add(budget.category)
            } else {
                budgetMap[budget.entityId] = mutableSetOf(budget.category)
                subcategories[budget.category] = categoryAndEmojiRepository.getAllSavedSubCategories(budget.category).map { it.name }.toMutableSet()
            }
        }
    }

    private suspend fun fetchDataAndUpdate(groupId: String) {
        val group = groupDao.getGroupByGroupId(groupId)
        fetchBudgetAndUpdate(group)
        fetchExpenseAndUpdate(group)
    }

    private suspend fun fetchBudgetAndUpdate(group: GroupEntity) {
        val groupId = group.groupId
        Log.d("$tag ::", "group :: ${group.toFireBaseModel()}")
        if (group.members.isNotEmpty()) {
            groupExpenseCollectionRef.document(groupId)
                .collection(Utils.BUDGET_DETAILS)
                .addSnapshotListener { budgets, e ->
                    if (e != null) {
                        Log.d("$tag ::", "groupBudget :: $e")
                    }
                    if (budgets != null) {
                        scope.launch(Dispatchers.IO) {
                            try {
                                if (!budgetMap.containsKey(groupId)) {
                                    budgetMap[groupId] = mutableSetOf()
                                }
                                val setOfExistingBudgets = budgetMap[groupId]
                                for (budget in budgets) {
                                    val budgetEntity = budget.toObject(BudgetEntity::class.java)
                                    budgetEntity.uploadedOnServer = true
                                    if (!setOfExistingBudgets!!.contains(budget.id)) {
                                        budgetEntity.uploadedOnServer = true
                                        budgetMap[groupId]!!.add(budgetEntity.category)
                                        budgetRepository.addBudgetFromServer(budgetEntity)
                                        categoryAndEmojiRepository.addCategory(
                                            CategoryEntity(
                                                name = budgetEntity.category,
                                                icon = budgetEntity.icon
                                            )
                                        )
                                    } else {
                                        budgetRepository.updateBudget(entity = budgetEntity)
                                    }
                                }
                            } catch (e: Exception) {
                                Log.d("$tag ::", e.toString())
                            }
                        }
                    }
                }
        }
    }

    private suspend fun fetchExpenseAndUpdate(group: GroupEntity) {
        var listenerRegistration: ListenerRegistration? = null
        listenerRegistration =
            groupExpenseCollectionRef.document(group.groupId) // register a listener to read expenses from the last timestamp
                .collection(Utils.EXPENSES)
                .whereGreaterThan(Utils.TIME, group.lastUpdate)
                .orderBy(Utils.TIME, Query.Direction.ASCENDING)
                .addSnapshotListener { documents, e ->
                    if (e != null) {
                        Log.d("$tag ::", "groupExpenseError :: $e")
                    }
                    if (documents != null) {
                        scope.launch {
                            val newExpenses = documents.mapNotNull { document ->
                                val expenseEntity = document.toObject(ExpenseEntity::class.java)
                                expenseEntity.uploadedOnServer = true
                                expenseEntity.isSentTransaction = auth.currentUser?.uid == expenseEntity.expensorId
                                getAndAddSubCategoryFromFirebaseExpense(expenseEntity)
                                expenseEntity
                            }
                            Log.d(
                                "$tag ::",
                                "groupExpenseList :: ${newExpenses.size} && ${group.lastUpdate}"
                            )
                            if (newExpenses.isNotEmpty()) { // if list is not empty, at the end we will remove the listener and start it with the updated last time stamp
                                expenseRepository.insertBulkExpenseFromServer(newExpenses)
                                group.lastUpdate = newExpenses.last().time
                                groupDao.updateEntity(group)
                                listenerRegistration?.remove()
                                fetchExpenseAndUpdate(group)
                            }
                        }
                    }
                }
    }

    private suspend fun getAndAddSubCategoryFromFirebaseExpense(expenseEntity: ExpenseEntity) {
        if (expenseEntity.subCategory.isNotEmpty() &&
            (!subcategories.containsKey(expenseEntity.category) || !subcategories[expenseEntity.category]!!.contains(
                expenseEntity.subCategory
            ))
        ) {
            var needAddSubcategoryToDb = true
            if (subcategories.containsKey(expenseEntity.category)) {
                if (subcategories[expenseEntity.category]!!.add(
                        expenseEntity.subCategory
                    )
                )
                    needAddSubcategoryToDb = false
            } else {
                subcategories[expenseEntity.category] =
                    mutableSetOf(expenseEntity.subCategory)
            }
            if (needAddSubcategoryToDb) {
                categoryAndEmojiRepository.addSubCategory(
                    SubCategoryEntity(
                        name = expenseEntity.subCategory,
                        icon = expenseEntity.icon,
                        category = expenseEntity.category
                    )
                )
            }
        }
    }

    private suspend fun downloadGroupImage(group: GroupEntity) {
        Log.d("downloadGroupImage", group.groupId)
        if (!groupDao.doesGroupExists(groupId = group.groupId)) {
            Log.d("downloadGroupImage", "group not present")
            val inputData = Data.Builder()
                .putString("firebaseImagePath", Utils.GROUP_IMAGE)
                .putString("imageId", group.groupId)
                .build()

            // Create a WorkRequest
            val workRequest = OneTimeWorkRequestBuilder<ImageDownloadWorker>()
                .setInputData(inputData)
                .build()

            // Enqueue the WorkRequest
            val workManager = WorkManager.getInstance(context)
            workManager.enqueue(workRequest)
            withContext(Dispatchers.Main) {
                workManager.getWorkInfoByIdLiveData(workRequest.id).observeForever { workInfo ->
                    if (workInfo != null && workInfo.state.isFinished) {
                        val result = workInfo.outputData.getString("resultKey")
                        group.localImagePath = result ?: ""
                        scope.launch {
                            groupDao.updateEntity(group)
                        }
                    }
                }
            }
        }
    }

}