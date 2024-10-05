package com.penny.planner.data.repositories.interfaces

interface BudgetRepository {
    suspend fun addBudget(category: String, icon: String, spendLimit: Double, entityId: String)
    suspend fun isBudgetAvailable(entityId: String, category: String): Boolean
}