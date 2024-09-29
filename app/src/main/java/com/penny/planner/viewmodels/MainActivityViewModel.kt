package com.penny.planner.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.penny.planner.data.repositories.interfaces.DataStoreBudgetRepository
import com.penny.planner.data.repositories.interfaces.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val budgetRepository: DataStoreBudgetRepository
): ViewModel() {

    fun getIsUserLoggedIn() = userRepository.isLoggedIn()
    fun getOnboardingNavigation() = userRepository.navigateToOnBoardingScreen()
    suspend fun getBudget() = budgetRepository.getBudget()
    fun setBudget(amount: String) {
        viewModelScope.launch {
            budgetRepository.updateBudget(amount)
        }
    }
    fun getName() = userRepository.getUserName()
}