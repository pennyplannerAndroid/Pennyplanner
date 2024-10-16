package com.penny.planner.data.repositories.implementations

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.penny.planner.data.db.budget.BudgetDao
import com.penny.planner.data.db.budget.BudgetEntity
import com.penny.planner.data.repositories.interfaces.BudgetRepository
import com.penny.planner.helpers.Utils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class BudgetRepositoryImpl @Inject constructor(
    private val budgetDao: BudgetDao
): BudgetRepository {
    val db = FirebaseFirestore.getInstance()
    @Inject lateinit var applicationScope: CoroutineScope

    private val budgetCollectionRef = db.collection(Utils.USER_EXPENSES)
        .document(FirebaseAuth.getInstance().currentUser!!.uid)
        .collection(Utils.BUDGET_DETAILS)

    private val groupExpenseCollectionRef = db.collection(Utils.GROUP_EXPENSES)

    override suspend fun createBudgetLocally(
        category: String,
        icon: String,
        spendLimit: Double,
        entityId: String
    ) {
        val budgetEntity = BudgetEntity(
            category = category,
            icon = icon,
            spendLimit = spendLimit,
            entityId = if (entityId == "") FirebaseAuth.getInstance().currentUser!!.uid else entityId
        )
        budgetDao.addBudgetItem(budgetEntity)
        if (entityId.isNotEmpty())
           addBudgetForGroup(budgetEntity)
        else
            addBudgetForSelf(budgetEntity)
    }

    private suspend fun addBudgetForSelf(entity: BudgetEntity) {
        budgetCollectionRef.document(entity.category).set(entity.toFireBaseEntity())
            .addOnSuccessListener {
                entity.uploadedOnServer = true
                applicationScope.launch(Dispatchers.IO) {
                    budgetDao.updateEntity(entity)
                }
            }
    }

    private suspend fun addBudgetForGroup(entity: BudgetEntity) {
        groupExpenseCollectionRef
            .document(entity.entityId)
            .collection(Utils.BUDGET_DETAILS)
            .document(entity.category)
            .set(entity.toFireBaseEntity())
            .addOnSuccessListener {
                entity.uploadedOnServer = true
                applicationScope.launch(Dispatchers.IO) {
                    budgetDao.updateEntity(entity)
                }
            }
    }

    override suspend fun isBudgetAvailable(entityId: String, category: String): Boolean {
        val id = if (entityId == "") FirebaseAuth.getInstance().currentUser!!.uid else entityId
        return budgetDao.isBudgetAvailable(entityId = id, category = category)
    }

    override suspend fun addBudgetFromServer(entity: BudgetEntity) {
        budgetDao.addBudgetItem(entity)
    }

    override suspend fun insertBudgetListFromServer(list: List<BudgetEntity>) {
        budgetDao.addBudgetList(list)
    }

    override suspend fun updateBudget(entity: BudgetEntity) {
        budgetDao.updateEntity(entity)
    }

    override suspend fun getAllBudgets(): List<BudgetEntity> = budgetDao.getAllBudgets()
}