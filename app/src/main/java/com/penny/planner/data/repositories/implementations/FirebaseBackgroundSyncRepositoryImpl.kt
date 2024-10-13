package com.penny.planner.data.repositories.implementations

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.penny.planner.data.db.budget.BudgetDao
import com.penny.planner.data.db.budget.BudgetEntity
import com.penny.planner.data.db.category.CategoryEntity
import com.penny.planner.data.db.expense.ExpenseDao
import com.penny.planner.data.db.expense.ExpenseEntity
import com.penny.planner.data.db.groups.GroupDao
import com.penny.planner.data.db.groups.GroupEntity
import com.penny.planner.data.db.subcategory.SubCategoryEntity
import com.penny.planner.data.repositories.interfaces.CategoryAndEmojiRepository
import com.penny.planner.data.repositories.interfaces.FirebaseBackgroundSyncRepository
import com.penny.planner.data.repositories.interfaces.FriendsDirectoryRepository
import com.penny.planner.helpers.Utils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseBackgroundSyncRepositoryImpl @Inject constructor(
    private val groupDao: GroupDao,
    private val budgetDao: BudgetDao,
    private val expenseDao: ExpenseDao,
    private val categoryAndEmojiRepository: CategoryAndEmojiRepository,
    private val usersRepository: FriendsDirectoryRepository
): FirebaseBackgroundSyncRepository {

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
        budgetDao.addBudgetList(budgets)
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
                if (expenseEntity != null && expenseEntity.subCategory.isNotEmpty() &&
                    (!subcategories.containsKey(expenseEntity.category) || !subcategories[expenseEntity.category]!!.contains(expenseEntity.subCategory))) {
                    var needAddSubcategoryToDb = true
                    if (subcategories.containsKey(expenseEntity.category)) {
                        if (subcategories[expenseEntity.category]!!.add(expenseEntity.subCategory))
                            needAddSubcategoryToDb = false
                    } else {
                        subcategories[expenseEntity.category] = mutableSetOf(expenseEntity.subCategory)
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
                expenseEntity
            }
            expenseDao.insert(allExpenses)
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
            .child(source).addValueEventListener(object: ValueEventListener {
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
            .child(groupId).addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val entity = snapshot.getValue(GroupEntity::class.java)
                        if (entity != null) {
                            scope.launch {
                                groupDao.addGroup(entity)
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
                val userResult = usersRepository.findUser(email)
                if (userResult.isSuccess) {
                    usersRepository.addFriend(userResult.getOrNull()!!)
                }
            }
        }
    }

    private fun updatePendingNode(entity: GroupEntity) {
        userDirectory
            .child(Utils.formatEmailForFirebase(auth.currentUser!!.email!!))
            .child(Utils.GROUP_INFO)
            .child(Utils.JOINED).child(entity.groupId).setValue(Utils.NON_ADMIN_VALUE)
        userDirectory
            .child(Utils.formatEmailForFirebase(auth.currentUser!!.email!!))
            .child(Utils.GROUP_INFO)
            .child(Utils.PENDING).child(entity.groupId).removeValue()
    }

    private suspend fun generateLocalMap() {
        val budgets = budgetDao.getAllBudgets()
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
        groupExpenseCollectionRef.document(groupId)
            .collection(Utils.BUDGET_DETAILS)
            .addSnapshotListener{ budgets, e ->
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
                                    budgetDao.addBudgetItem(budgetEntity)
                                    categoryAndEmojiRepository.addCategory(
                                        CategoryEntity(
                                            name = budgetEntity.category,
                                            icon = budgetEntity.icon
                                        )
                                    )
                                } else {
                                    budgetDao.updateEntity(entity = budgetEntity)
                                }
                            }
                        } catch (e: Exception) {
                            Log.d("$tag ::", e.toString())
                        }
                    }
                }
            }
    }

    private suspend fun fetchExpenseAndUpdate(group: GroupEntity) {
        var listenerRegistration: ListenerRegistration? = null
        listenerRegistration = groupExpenseCollectionRef.document(group.groupId) // register a listener to read expenses from the last timestamp
            .collection(Utils.EXPENSES)
            .whereGreaterThan(FieldPath.documentId(), group.lastUpdate.toString())
            .addSnapshotListener { documents, e ->
                if (e != null) {
                    Log.d("$tag ::", "groupExpenseError :: $e")
                }
                if (documents != null) {
                    val newExpenses = documents.mapNotNull { document ->
                        val expenseEntity = document.toObject(ExpenseEntity::class.java)
                        expenseEntity.uploadedOnServer = true
                        if (expenseEntity.subCategory.isNotEmpty() &&
                            (!subcategories.containsKey(expenseEntity.category) || !subcategories[expenseEntity.category]!!.contains(expenseEntity.subCategory))) {
                            var needAddSubcategoryToDb = true
                            if (subcategories.containsKey(expenseEntity.category)) {
                                if (subcategories[expenseEntity.category]!!.add(expenseEntity.subCategory))
                                    needAddSubcategoryToDb = false
                            } else {
                                subcategories[expenseEntity.category] = mutableSetOf(expenseEntity.subCategory)
                            }
                            if (needAddSubcategoryToDb) {
                                scope.launch {
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
                        expenseEntity
                    }
                    Log.d("$tag ::", "groupExpenseList :: ${newExpenses.size} && ${group.lastUpdate}")
                    scope.launch {
                        if (expenseDao.isExpenseAvailable(group.groupId) > 0) { // not a fresh login so we will only add those expenses which are not local
                            val filteredList =
                                newExpenses.filter { it.expensorId != auth.currentUser!!.uid }
                            if (filteredList.isNotEmpty()) { // if list is not empty, we will remove the listener and start it with the updated last time stamp
                                group.lastUpdate = filteredList.maxOfOrNull { it.time } ?: group.lastUpdate
                                groupDao.updateEntity(group)
                                listenerRegistration?.remove()
                                fetchExpenseAndUpdate(group)
                            }
                            expenseDao.insert(filteredList)
                        } else { // fresh install - we will add all expenses and update the timestamp
                            if (newExpenses.isNotEmpty()) {
                                group.lastUpdate = newExpenses.maxOfOrNull { it.time } ?: group.lastUpdate
                                groupDao.updateEntity(group)
                                expenseDao.insert(newExpenses)
                                listenerRegistration?.remove()
                                fetchExpenseAndUpdate(group)
                            }
                        }
                    }
                }
            }
    }

}