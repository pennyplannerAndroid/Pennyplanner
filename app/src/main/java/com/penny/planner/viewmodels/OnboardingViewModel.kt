package com.penny.planner.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.penny.planner.data.repositories.interfaces.MonthlyBudgetRepository
import com.penny.planner.data.repositories.interfaces.OnboardingRepository
import com.penny.planner.helpers.enums.LoginResult
import com.penny.planner.models.MonthlyBudgetInfoModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val repository : OnboardingRepository,
    private val budgetRepository: MonthlyBudgetRepository
): ViewModel() {
    //login
    private val _loginResult = MutableLiveData<Result<LoginResult>>()
    val loginResult : LiveData<Result<LoginResult>> = _loginResult
    //signup
    private val _signupResult = MutableLiveData<Result<Boolean>>()
    val signUpResult: LiveData<Result<Boolean>> = _signupResult
    //profile update
    private val _profileUpdateResult = MutableLiveData<Result<Boolean>>()
    var profileUpdateResult: LiveData<Result<Boolean>> = _profileUpdateResult
    //email verification link send and verification check
    private val _emailSent = MutableLiveData<Result<Boolean>>()
    val emailSent = _emailSent
    private val _isVerified = MutableLiveData<Result<Boolean>>()
    val isVerified = _isVerified
    //forget password
    private val _forgetPasswordSentStatus = MutableLiveData<Result<Boolean>>()
    val forgetPasswordSentStatus = _forgetPasswordSentStatus
    // set monthly budget
    private val _monthlyBudgetStatus = MutableLiveData<Result<Boolean>>()
    val monthlyBudgetStatus = _monthlyBudgetStatus

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginResult.value = repository.login(email, password)
        }
    }

    fun loginWithGoogle() {

    }

    fun loginWithFacebook() {

    }

    fun signup(email: String, password: String) {
        viewModelScope.launch {
            _signupResult.value = repository.signup(email, password)
        }
    }

    fun signupWithGoogle() {

    }

    fun signupWithFacebook() {
        viewModelScope.launch {

        }
    }

    fun updateProfile(name: String, byteArray: ByteArray?) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = repository.updateProfile(name, byteArray)
            withContext(Dispatchers.Main) {
                _profileUpdateResult.value = result
            }
        }
    }

    fun setMonthlyLimit(monthlyBudgetInfo: MonthlyBudgetInfoModel) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = budgetRepository.updateMonthlyBudget(monthlyBudgetInfo)
            withContext(Dispatchers.Main) {
                _monthlyBudgetStatus.value = result
            }
        }
    }

    fun sendVerificationEmail() {
        viewModelScope.launch {
            _emailSent.value = repository.sendVerificationEmail()
        }
    }

    fun checkVerificationStatus() {
        viewModelScope.launch {
            _isVerified.value = repository.isEmailVerified()
        }
    }

    fun sendForgetPasswordEmail(email: String) {
        viewModelScope.launch {
            _forgetPasswordSentStatus.value = repository.sendPasswordResetEmail(email)
        }
    }

    fun getEmail(): String {
        return repository.getEmailId()
    }

    fun getName(): String {
        return repository.getName()
    }

}