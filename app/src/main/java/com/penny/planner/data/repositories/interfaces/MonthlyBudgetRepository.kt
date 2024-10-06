package com.penny.planner.data.repositories.interfaces

interface MonthlyBudgetRepository {
    suspend fun updateMonthlyBudget(amount: String): Result<Boolean>
    suspend fun updateLocalWithMonthlyBudget(amount: String)
    suspend fun getMonthlyBudget(): String?
}