package com.penny.planner.data.repositories.interfaces

interface DataStoreBudgetRepository {
    suspend fun updateBudget(amount: String)
    suspend fun getBudget(): String?
}