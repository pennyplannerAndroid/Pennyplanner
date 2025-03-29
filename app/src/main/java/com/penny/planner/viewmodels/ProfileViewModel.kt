package com.penny.planner.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.penny.planner.data.repositories.interfaces.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {
    //profile update
    private val _profileUpdateResult = MutableLiveData<Result<Boolean>?>()
    var profileUpdateResult: LiveData<Result<Boolean>?> = _profileUpdateResult

    suspend fun getSelfProfile() = userRepository.getSelfProfile()

    fun logout() {
        userRepository.logout()
    }

    fun updateProfile(name: String, byteArray: ByteArray?, isNameChanged: Boolean, isImageChanged: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = userRepository.updateProfile(name, byteArray, isNameChanged, isImageChanged)
            withContext(Dispatchers.Main) {
                _profileUpdateResult.value = result
            }
        }
    }

    fun resetProfileUpdateResult() {
        _profileUpdateResult.value = null
    }


}