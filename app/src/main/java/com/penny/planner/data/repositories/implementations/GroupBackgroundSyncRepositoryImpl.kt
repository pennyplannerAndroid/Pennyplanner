package com.penny.planner.data.repositories.implementations

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.penny.planner.data.db.budget.BudgetDao
import com.penny.planner.data.db.budget.BudgetEntity
import com.penny.planner.data.db.category.CategoryDao
import com.penny.planner.data.db.category.CategoryEntity
import com.penny.planner.data.db.expense.ExpenseDao
import com.penny.planner.data.db.expense.ExpenseEntity
import com.penny.planner.data.db.groups.GroupDao
import com.penny.planner.data.db.groups.GroupEntity
import com.penny.planner.data.db.subcategory.SubCategoryDao
import com.penny.planner.data.db.subcategory.SubCategoryEntity
import com.penny.planner.data.repositories.interfaces.GroupBackgroundSyncRepository
import com.penny.planner.helpers.Utils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class GroupBackgroundSyncRepositoryImpl @Inject constructor(
    private val groupDao: GroupDao,
    private val budgetDao: BudgetDao,
    private val subCategoryDao: SubCategoryDao,
    private val categoryDao: CategoryDao,
    private val expenseDao: ExpenseDao
): GroupBackgroundSyncRepository {

    val scope = CoroutineScope(Job() + Dispatchers.IO)
    private val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    private val groupExpenseCollectionRef = db.collection(Utils.GROUP_EXPENSES)
    private val userDirectory = FirebaseDatabase.getInstance().getReference(Utils.USERS)
    private val budgetMap = mutableMapOf<String, MutableSet<String>>()
    private val subcategories = mutableMapOf<String, MutableSet<String>>()

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
                                fetchDataAndUpdate(entity.groupId)
                                if (needUpdatePendingList)
                                    updatePendingNode(entity)
                            }
                        }
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    Log.d("GroupRepository:: ", error.message)
                }
            })
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
                subcategories[budget.category] = subCategoryDao.getAllSubCategoryName(budget.category).toMutableSet()
            }
        }
    }

    private suspend fun fetchDataAndUpdate(groupId: String) {
        val group = groupDao.getGroupByGroupId(groupId)
        fetchBudgetAndUpdate(group)
        groupExpenseCollectionRef.document(groupId)
            .collection(Utils.VERSION_DETAILS).document(Utils.EXPENSE_VERSION)
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Log.d("GroupSync ::", "expense :: $e")
                }
                if (snapshots != null) {
                    val serverVersion = snapshots.data?.get("value") as Long? ?: 0L
                    Log.d("GroupSync ::", "fetchExpenseVersion :: $serverVersion")
                    scope.launch(Dispatchers.IO) {
                        if (serverVersion > group.expenseVersion) {
                            fetchExpenseAndUpdate(group)
                        }
                    }
                }
            }
    }

    private suspend fun fetchBudgetAndUpdate(group: GroupEntity) {
        val groupId = group.groupId
        groupExpenseCollectionRef.document(groupId)
            .collection(Utils.BUDGET_DETAILS)
            .addSnapshotListener{ budgets, e ->
                if (e != null) {
                    Log.d("GroupSync ::", "budget :: $e")
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
                                if (!setOfExistingBudgets!!.contains(budget.id)) {
                                    budgetEntity.uploadedOnServer = true
                                    budgetMap[groupId]!!.add(budgetEntity.category)
                                    budgetDao.addBudgetItem(budgetEntity)
                                    categoryDao.insert(
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
                            Log.d("GroupSync ::", e.toString())
                        }
                    }
                }
            }
    }

    private suspend fun fetchExpenseAndUpdate(group: GroupEntity) {
        try {
            val expensesQuery = groupExpenseCollectionRef.document(group.groupId)
                .collection(Utils.EXPENSES)
                .whereGreaterThan(FieldPath.documentId(), group.lastUpdate.toString())
                .get()
                .await()
            val newExpenses = expensesQuery.documents.mapNotNull { document ->
                val expenseEntity = document.toObject(ExpenseEntity::class.java)
                if (expenseEntity != null && expenseEntity.subCategory.isNotEmpty() && !subcategories.containsKey(expenseEntity.subCategory)) {
                    subCategoryDao.addSubCategory(
                        SubCategoryEntity(
                            name = expenseEntity.subCategory,
                            icon = expenseEntity.icon,
                            category = expenseEntity.category
                        )
                    )
                }
                expenseEntity
            }.filter { it.expensorId != auth.currentUser!!.uid }
            group.lastUpdate = newExpenses.maxOfOrNull{ it.time } ?: group.lastUpdate
            groupDao.updateEntity(group)
            expenseDao.insert(newExpenses)
            Log.d("GroupSync ::", "expense :: ${expensesQuery.size()}")
        } catch (e: Exception) {
            Log.d("GroupSync ::", "expenseError :: $e")
        }
    }

}