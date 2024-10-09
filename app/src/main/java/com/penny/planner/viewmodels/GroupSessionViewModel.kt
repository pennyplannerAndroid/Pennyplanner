package com.penny.planner.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.penny.planner.data.db.expense.ExpenseEntity
import com.penny.planner.data.repositories.interfaces.ExpenseRepository
import com.penny.planner.data.repositories.interfaces.GroupRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GroupSessionViewModel @Inject constructor(
    private val expenseRepository: ExpenseRepository,
    private val groupRepository: GroupRepository
) : ViewModel() {

    private var groupId = ""

    fun setGroupId(groupId: String) {
        this.groupId = groupId
    }

    suspend fun getGroup() = groupRepository.getGroupById(groupId)

    suspend fun getAllExpenses() = expenseRepository.getAllExpenses(groupId)

    fun addExpense(expense: ExpenseEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            expense.groupId = groupId
            expenseRepository.addExpense(expense)
        }
    }

    fun addMessage(message: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val expense = ExpenseEntity(
                content = message,
                entityType = 0,
                groupId = groupId
            )
            expenseRepository.addExpense(expense)
        }
    }

}