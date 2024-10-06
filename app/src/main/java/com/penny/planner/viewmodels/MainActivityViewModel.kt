package com.penny.planner.viewmodels

import androidx.lifecycle.ViewModel
import com.penny.planner.data.repositories.interfaces.MonthlyBudgetRepository
import com.penny.planner.data.repositories.interfaces.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val budgetRepository: MonthlyBudgetRepository
): ViewModel() {

    fun getIsUserLoggedIn() = userRepository.isLoggedIn()
    suspend fun getIsBudgetSet(): Boolean {
        return budgetRepository.getMonthlyBudget() != null
    }
    fun getOnboardingNavigation() = userRepository.navigateToOnBoardingScreen()
    fun getName() = userRepository.getUserName()
}