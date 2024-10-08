package com.penny.planner.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.penny.planner.data.db.expense.ExpenseEntity
import com.penny.planner.data.repositories.interfaces.ExpenseRepository
import com.penny.planner.data.repositories.interfaces.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExpenseViewModel @Inject constructor(
    private val expenseRepository: ExpenseRepository,
    private val userRepository: UserRepository
): ViewModel() {

    fun getName() = userRepository.getUserName()
    fun getPicturePath() = userRepository.getImagePath()

    fun addExpense(entity: ExpenseEntity) {
        viewModelScope.launch {
            expenseRepository.addExpense(entity)
        }
    }

    suspend fun getAllExpense() = expenseRepository.getAllExpenses()
}