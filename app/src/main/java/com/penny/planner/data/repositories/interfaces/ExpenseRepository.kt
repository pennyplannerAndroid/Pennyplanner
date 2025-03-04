package com.penny.planner.data.repositories.interfaces

import androidx.lifecycle.LiveData
import com.penny.planner.data.db.expense.ExpenseEntity
import com.penny.planner.data.db.monthlyexpenses.MonthlyExpenseEntity
import com.penny.planner.models.GroupDisplayModel

interface ExpenseRepository {
    suspend fun addExpense(entity: ExpenseEntity)
    suspend fun getAllExpensesExceptMessage(groupId: String, start: Long, end: Long): List<GroupDisplayModel>
    suspend fun getExpensesForDisplayAtHomePage(): LiveData<List<ExpenseEntity>>
    suspend fun getAllExpenses(groupId: String): LiveData<List<GroupDisplayModel>>
    suspend fun insertBulkExpenseFromServer(list: List<ExpenseEntity>)
    suspend fun getMonthlyExpenseEntity(id: String, month: String): MonthlyExpenseEntity?
}