package com.penny.planner.data.repositories.interfaces

import com.penny.planner.data.db.monthlyexpenses.MonthlyExpenseEntity

interface MonthlyExpenseRepository {
    suspend fun getMonthlyExpenseEntity(entityId: String, month: String): MonthlyExpenseEntity?
    suspend fun addMonthlyExpenseEntity(monthlyExpenseEntity: MonthlyExpenseEntity)
    suspend fun updateExpenseEntity(monthlyExpenseEntity: MonthlyExpenseEntity)
    suspend fun removeExpenseDataForGroup(groupId: String)
}