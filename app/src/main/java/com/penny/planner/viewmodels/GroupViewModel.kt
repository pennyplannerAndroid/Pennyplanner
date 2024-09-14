package com.penny.planner.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.penny.planner.data.repositories.interfaces.GroupRepository
import com.penny.planner.helpers.Utils
import com.penny.planner.models.GroupModel
import com.penny.planner.models.UserModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GroupViewModel @Inject constructor(
    private val groupRepository: GroupRepository
): ViewModel() {

    private val _validateEmailResult = MutableLiveData<Result<Boolean>>()
    val validateEmailResult: LiveData<Result<Boolean>> = _validateEmailResult

    private val _searchEmailResult = MutableLiveData<Result<UserModel>>()
    val searchEmailResult: LiveData<Result<UserModel>> = _searchEmailResult

    private val _newGroupResult = MutableLiveData<Result<Boolean>>()
    val newGroupResult: LiveData<Result<Boolean>> = _newGroupResult

    fun findUser(email: String) {
        viewModelScope.launch {
            _searchEmailResult.value = groupRepository.findUser(email)
        }
    }

    fun resetFoundFriend() {
        _searchEmailResult.value = Result.failure(Exception(Utils.USER_NOT_FOUND))
    }

    fun newGroup(group: GroupModel, byteArray: ByteArray?) {
        viewModelScope.launch {
            _newGroupResult.value = groupRepository.newGroup(group, byteArray)
        }
    }
}