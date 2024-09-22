package com.penny.planner.data.repositories.implementations

import androidx.lifecycle.LiveData
import com.google.firebase.auth.FirebaseAuth
import com.penny.planner.data.db.expense.ExpenseDao
import com.penny.planner.data.db.expense.ExpenseEntity
import com.penny.planner.data.repositories.interfaces.ExpenseRepository
import javax.inject.Inject

class ExpenseRepositoryImpl @Inject constructor(
    private val expenseDao: ExpenseDao
) : ExpenseRepository {

    private val auth = FirebaseAuth.getInstance()

    override suspend fun getAllExpenses(): LiveData<List<ExpenseEntity>> = expenseDao.getAllExpenses()

    override suspend fun addExpense(entity: ExpenseEntity) {
        entity.expensorId = auth.currentUser?.uid ?: ""
        expenseDao.insert(entity)
    }

}