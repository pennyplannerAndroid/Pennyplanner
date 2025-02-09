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
            scope.launch(Dispatchers.IO) {
                fetchPersonalBudgetAndUpdate()
                fetchPersonalExpenseAndUpdate()
            }
        }
    }

    override fun getAllJoinedGroupsFromFirebaseAtLogin() {
        if (auth.currentUser != null) {
            scope.launch(Dispatchers.IO) {
                fetchAllJoinedGroupsFromFirebase()
                addListenerToPendingGroupNode()
            }
        }
    }

    override fun init() {
        addListenerToAllAddedGroups()
        getAllPendingGroups()
        updateGroupsTransactions()
    }

    override fun addGroupForFirebaseListener(groupId: String) {
        scope.launch(Dispatchers.IO) {
            addListenerToJoinedGroupInfo(groupId)
        }
    }

    override suspend fun newSubCategoryAdded(entity: SubCategoryEntity) {
        if (subcategories.containsKey(entity.category)) {
            subcategories[entity.category]!!.add(entity.name)
        } else {
            subcategories[entity.category] = mutableSetOf(entity.name)
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
                expenseEntity?.isSentTransaction =
                    auth.currentUser?.uid == expenseEntity?.expensorId
                if (expenseEntity != null)
                    getAndAddSubCategoryFromFirebaseExpense(expenseEntity)
                expenseEntity
            }
            expenseRepository.insertBulkExpenseFromServer(allExpenses)
        } catch (e: Exception) {
            Log.d("$tag ::", "personalExpense :: $e")
        }
    }

    private suspend fun fetchAllJoinedGroupsFromFirebase() {
        val joinedGroups: Map<*, *>? = userDirectory
            .child(Utils.formatEmailForFirebase(auth.currentUser!!.email!!))
            .child(Utils.GROUP_INFO)
            .child(Utils.JOINED).get().await().value as Map<*, *>?
        if (joinedGroups != null) {
            for (groupId in joinedGroups.keys) {
                addListenerToJoinedGroupInfo(groupId = groupId.toString())
            }
        }
    }

    private fun addListenerToAllAddedGroups() {
        scope.launch {
            val groups = groupDao.getAllExistingGroupsFromDb()
            if (groups.isNotEmpty()) {
                for (group in groups) {
                    if (!group.isPending)
                        addListenerToJoinedGroupInfo(groupId = group.groupId)
                }
            }
        }
    }

    private suspend fun addListenerToJoinedGroupInfo(groupId: String) {
        FirebaseDatabase.getInstance()
            .getReference(Utils.GROUPS)
            .child(groupId).child(Utils.GROUP_INFO)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val entity = snapshot.getValue(GroupEntity::class.java)
                        if (entity != null) {
                            scope.launch {
                                entity.isPending = false
                                if (groupDao.doesGroupExists(groupId)) {
                                    val existingGroup = groupDao.getGroupByGroupId(groupId)
                                    if (existingGroup.profileImage != entity.profileImage) {
                                        downloadGroupImage(entity)
                                    }
                                    entity.hasPendingMembers = existingGroup.hasPendingMembers
                                    entity.lastUpdate = existingGroup.lastUpdate
                                    groupDao.updateEntity(entity = entity)
                                } else {
                                    downloadGroupImage(entity)
                                    groupDao.addGroup(entity)
                                    fetchDataAndUpdate(entity.groupId)
                                }
                                updateFriendsDb(entity.members)
                                if (monthlyExpenseRepository.getMonthlyExpenseEntity(
                                        entityId = groupId,
                                        month = Utils.getCurrentMonthYear()
                                    ) == null
                                ) {
                                    monthlyExpenseRepository.addMonthlyExpenseEntity(
                                        MonthlyExpenseEntity(
                                            entityID = groupId,
                                            month = Utils.getCurrentMonthYear(),
                                            expense = 0.0
                                        )
                                    )
                                }
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("$tag :: ", error.message)
                }
            })
    }

    private fun addListenerToPendingGroupNode() {
        userDirectory
            .child(Utils.formatEmailForFirebase(auth.currentUser!!.email!!))
            .child(Utils.GROUP_INFO)
            .child(Utils.PENDING).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val groupNode = snapshot.value as Map<*, *>
                        if (groupNode.isNotEmpty()) {
                            scope.launch(Dispatchers.IO) {
                                for (node in groupNode) {
                                    if (node.value == 0L) {
                                        if (!groupDao.doesGroupExists(node.key.toString())) {
                                            getPendingGroupDetailAndAdd(node.key.toString())
                                        }
                                    } else if (node.value == 1L) {
                                        addListenerToJoinedGroupInfo(node.key.toString())
                                        updateGroupNodeAfterAdminApproval(node.key.toString())
                                    } else if (node.value == 2L) {
                                        deleteGroupFromLocalAndServer(node.key.toString())
                                    }
                                }
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("GroupRepository:: ", error.message)
                }
            })
    }

    private suspend fun getPendingGroupDetailAndAdd(groupId: String) {
        val group = FirebaseDatabase.getInstance()
            .getReference(Utils.GROUPS)
            .child(groupId)
            .child(Utils.GROUP_INFO)
            .get()
            .await()
            .getValue(GroupEntity::class.java)
        if (group != null) {
            group.isPending = true
            groupDao.addGroup(group)
        }
    }

    private fun getAllPendingGroups() {
        if (auth.currentUser != null) {
            scope.launch {
                addListenerToPendingGroupNode()
            }
        }
    }

    private fun updateGroupsTransactions() {
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

    private suspend fun deleteGroupFromLocalAndServer(groupId: String) {
        groupDao.delete(groupId)
        monthlyExpenseRepository.removeExpenseDataForGroup(groupId)
        userDirectory
            .child(Utils.formatEmailForFirebase(auth.currentUser!!.email!!))
            .child(Utils.GROUP_INFO)
            .child(Utils.PENDING).child(groupId).removeValue()
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

    private fun updateGroupNodeAfterAdminApproval(groupId: String) {
        userDirectory
            .child(Utils.formatEmailForFirebase(auth.currentUser!!.email!!))
            .child(Utils.GROUP_INFO)
            .child(Utils.JOINED).child(groupId).setValue(Utils.NON_ADMIN_VALUE)
        userDirectory
            .child(Utils.formatEmailForFirebase(auth.currentUser!!.email!!))
            .child(Utils.GROUP_INFO)
            .child(Utils.PENDING).child(groupId).removeValue()
    }

    private suspend fun generateLocalMap() {
        val budgets = budgetRepository.getAllBudgets()
        for (budget in budgets) {
            if (budgetMap.containsKey(budget.entityId)) {
                budgetMap[budget.entityId]!!.add(budget.category)
            } else {
                budgetMap[budget.entityId] = mutableSetOf(budget.category)
                subcategories[budget.category] =
                    categoryAndEmojiRepository.getAllSavedSubCategories(budget.category)
                        .map { it.name }.toMutableSet()
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
                                expenseEntity.isSentTransaction =
                                    auth.currentUser?.uid == expenseEntity.expensorId
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
                        groupDao.updatePicturePath(group.groupId, group.localImagePath)
                    }
                }
            }
        }
    }

}