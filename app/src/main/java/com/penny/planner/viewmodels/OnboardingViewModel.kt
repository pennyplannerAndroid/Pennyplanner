package com.penny.planner.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.penny.planner.data.repositories.OnboardingRepository
import com.penny.planner.models.FirebaseUser
import com.penny.planner.models.LoginResultModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val repository : OnboardingRepository
): ViewModel() {
    //login
    private val _loginResult = MutableLiveData<Result<LoginResultModel>>()
    val loginResult : LiveData<Result<LoginResultModel>> = _loginResult
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

    fun login(firebaseUser: FirebaseUser) {
        viewModelScope.launch {
            _loginResult.value = repository.login(firebaseUser)
        }
    }

    fun loginWithGoogle() {

    }

    fun loginWithFacebook() {

    }

    fun signup(user: FirebaseUser) {
        viewModelScope.launch {
            _signupResult.value = repository.signup(user.email, user.password)
        }
    }

    fun signupWithGoogle() {
//        viewModelScope.launch {
//            var signInRequest = BeginSignInRequest.builder()
//                .setGoogleIdTokenRequestOptions(
//                    BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
//                        .setSupported(true)
//                        // Your server's client ID, not your Android client ID.
//                        .setServerClientId(getString(
//                            R.string.web_client_id_for_google
//                        ))
//                        // Only show accounts previously used to sign in.
//                        .setFilterByAuthorizedAccounts(true)
//                        .build()
//                )
//                .build()
//        }
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

}