package com.penny.planner.data.repositories.interfaces

import com.penny.planner.data.db.budget.BudgetEntity

interface BudgetRepository {
    suspend fun createBudgetLocally(category: String, icon: String, spendLimit: Double, entityId: String)
    suspend fun isBudgetAvailable(entityId: String, category: String): Boolean
    suspend fun addBudgetFromServer(entity: BudgetEntity)
    suspend fun insertBudgetListFromServer(list: List<BudgetEntity>)
    suspend fun updateBudgetAfterServerUpdate(entity: BudgetEntity)
    suspend fun getAllBudgets(): List<BudgetEntity>
    suspend fun getAllBudgetsForEntity(entityId: String): List<BudgetEntity>
}