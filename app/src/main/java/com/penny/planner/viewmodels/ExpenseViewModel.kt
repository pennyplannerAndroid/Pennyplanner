package com.penny.planner.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.penny.planner.data.db.expense.ExpenseEntity
import com.penny.planner.data.repositories.interfaces.ExpenseRepository
import com.penny.planner.data.repositories.interfaces.MonthlyBudgetRepository
import com.penny.planner.data.repositories.interfaces.UserRepository
import com.penny.planner.helpers.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExpenseViewModel @Inject constructor(
    private val expenseRepository: ExpenseRepository,
    private val userRepository: UserRepository,
    private val budgetRepository: MonthlyBudgetRepository
): ViewModel() {

    suspend fun needOnboardingNavigation(): String? {
        return userRepository.navigationToOnboardingNeeded()
            ?: if (budgetRepository.getMonthlyBudget() == null)
                Utils.SET_MONTHLY_BUDGET
            else
                null
    }

    fun getName() = userRepository.getUserName()
    suspend fun getPicturePath() = userRepository.getImagePath()
    fun getSelfId() = userRepository.getSelfId()

    fun addExpense(entity: ExpenseEntity) {
        viewModelScope.launch {
            expenseRepository.addExpense(entity)
        }
    }

    suspend fun getMonthlyExpenseEntity(entityId: String = userRepository.getSelfId(), time: String = Utils.getCurrentMonthYear()) =
        expenseRepository.getMonthlyExpenseEntity(entityId, time)

    suspend fun getMonthlyBudget() = budgetRepository.getMonthlyBudget()

    suspend fun getHomePageExpense() = expenseRepository.getExpensesForDisplayAtHomePage()
}