package com.penny.planner.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.penny.planner.data.LoginRepository
import com.penny.planner.models.FirebaseUser
import com.penny.planner.models.LoginResultModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: LoginRepository
) : ViewModel() {
    private val _result = MutableLiveData<Result<LoginResultModel>>()
    val result : LiveData<Result<LoginResultModel>> = _result

    fun login(firebaseUser: FirebaseUser) {
        viewModelScope.launch {
            Log.d("LoginViewModel :: ", "login")
            delay(1000)
            _result.value = repository.login(firebaseUser)
        }
    }

    fun loginWithGoogle() {

    }

    fun loginWithFacebook() {

    }

}