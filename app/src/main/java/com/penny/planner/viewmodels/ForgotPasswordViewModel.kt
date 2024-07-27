package com.penny.planner.viewmodels

import android.util.Log
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.penny.planner.data.ForgetPasswordRepository
import com.penny.planner.data.ForgetPasswordRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ForgotPasswordViewModel
    @Inject constructor (
        private val repository: ForgetPasswordRepository
    )
: ViewModel() {
    private val _sentStatus = MutableLiveData<Result<Boolean>>()
    val sentStatus = _sentStatus

    fun sendForgetPasswordEmail(email: String) {
        viewModelScope.launch {
            _sentStatus.value = repository.sendPasswordResetEmail(email)
        }
    }
}