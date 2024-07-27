package com.penny.planner.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.penny.planner.data.SignUpRepository
import com.penny.planner.models.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val repository: SignUpRepository
): ViewModel() {

    private val _signupResult = MutableLiveData<Result<Boolean>>()
    val signUpResult: LiveData<Result<Boolean>> = _signupResult

    fun signup(user: FirebaseUser) {
        viewModelScope.launch {
            _signupResult.value = repository.signup(user.email, user.password)
        }
    }

    fun signupWithGoogle() {
        viewModelScope.launch {

        }
    }

    fun signupWithFacebook() {
        viewModelScope.launch {

        }
    }

}