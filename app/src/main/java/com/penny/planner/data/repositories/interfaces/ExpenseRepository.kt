package com.penny.planner.data.repositories.interfaces

import androidx.lifecycle.LiveData
import com.penny.planner.data.db.expense.ExpenseEntity
import com.penny.planner.models.GroupDisplayModel

interface ExpenseRepository {
    suspend fun addExpense(entity: ExpenseEntity)
    suspend fun getAllExpenses(): LiveData<List<ExpenseEntity>>
    suspend fun getAllExpenses(groupId: String): LiveData<List<GroupDisplayModel>>
}