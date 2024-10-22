package com.penny.planner.data.repositories.implementations

import com.penny.planner.data.db.monthlyexpenses.MonthlyExpenseDao
import com.penny.planner.data.db.monthlyexpenses.MonthlyExpenseEntity
import com.penny.planner.data.repositories.interfaces.MonthlyExpenseRepository
import javax.inject.Inject

class MonthlyExpenseRepositoryImpl @Inject constructor(
    private val monthlyExpenseDao: MonthlyExpenseDao
): MonthlyExpenseRepository {

    override suspend fun getMonthlyExpenseEntity(entityId: String, month: String) =
        monthlyExpenseDao.getMonthlyExpenseEntity(entityId, month)

    override suspend fun addMonthlyExpenseEntity(monthlyExpenseEntity: MonthlyExpenseEntity) {
       monthlyExpenseDao.addEntity(monthlyExpenseEntity)
    }

    override suspend fun updateExpenseEntity(monthlyExpenseEntity: MonthlyExpenseEntity) {
        monthlyExpenseDao.updateEntity(monthlyExpenseEntity)
    }

}