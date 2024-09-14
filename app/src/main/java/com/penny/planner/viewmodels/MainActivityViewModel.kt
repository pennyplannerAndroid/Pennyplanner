package com.penny.planner.viewmodels

import androidx.lifecycle.ViewModel
import com.penny.planner.data.repositories.interfaces.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val userRepository: UserRepository
): ViewModel() {

    fun getIsUserLoggedIn() = userRepository.isLoggedIn()
    fun getOnboardingNavigation() = userRepository.navigateToOnBoardingScreen()
}