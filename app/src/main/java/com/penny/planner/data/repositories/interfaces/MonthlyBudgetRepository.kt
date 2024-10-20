package com.penny.planner.data.repositories.interfaces

import com.penny.planner.models.MonthlyBudgetInfoModel

interface MonthlyBudgetRepository {
    suspend fun updateMonthlyBudget(monthlyBudgetInfo: MonthlyBudgetInfoModel): Result<Boolean>
    suspend fun updateLocalWithMonthlyBudget(monthlyBudgetInfo: MonthlyBudgetInfoModel)
    suspend fun getMonthlyBudget(): MonthlyBudgetInfoModel?
}