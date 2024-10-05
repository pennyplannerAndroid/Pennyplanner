package com.penny.planner.data.repositories.implementations

import com.google.firebase.auth.FirebaseAuth
import com.penny.planner.data.db.budget.BudgetDao
import com.penny.planner.data.db.budget.BudgetEntity
import com.penny.planner.data.repositories.interfaces.BudgetRepository
import javax.inject.Inject

class BudgetRepositoryImpl @Inject constructor(
    private val budgetDao: BudgetDao
): BudgetRepository {

    override suspend fun addBudget(
        category: String,
        icon: String,
        spendLimit: Double,
        entityId: String
    ) {
        budgetDao.addBudgetItem(
            BudgetEntity(
                category = category,
                icon = icon,
                spendLimit = spendLimit,
                entityId = if (entityId == "") FirebaseAuth.getInstance().currentUser!!.uid else entityId
            )
        )
    }

    override suspend fun isBudgetAvailable(entityId: String, category: String): Boolean {
        val id = if (entityId == "") FirebaseAuth.getInstance().currentUser!!.uid else entityId
        return budgetDao.isBudgetAvailable(entityId = id, category = category)
    }
}