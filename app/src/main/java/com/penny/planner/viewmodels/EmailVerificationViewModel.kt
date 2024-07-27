package com.penny.planner.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.penny.planner.data.EmailVerificationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EmailVerificationViewModel @Inject constructor(
    private val repository: EmailVerificationRepository
) : ViewModel() {
    private val _emailSent = MutableLiveData<Result<Boolean>>()
    val emailSent = _emailSent

    private val _isVerified = MutableLiveData<Result<Boolean>>()
    val isVerified = _isVerified

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
}