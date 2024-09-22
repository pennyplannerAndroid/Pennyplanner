package com.penny.planner.data.repositories.interfaces

import androidx.lifecycle.LiveData
import com.penny.planner.data.db.expense.ExpenseEntity

interface ExpenseRepository {
    suspend fun addExpense(entity: ExpenseEntity)
    suspend fun getAllExpenses(): LiveData<List<ExpenseEntity>>
}