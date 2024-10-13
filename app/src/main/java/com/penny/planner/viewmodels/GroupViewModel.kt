package com.penny.planner.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.penny.planner.data.db.friends.UsersEntity
import com.penny.planner.data.repositories.interfaces.FriendsDirectoryRepository
import com.penny.planner.data.repositories.interfaces.GroupRepository
import com.penny.planner.helpers.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GroupViewModel @Inject constructor(
    private val groupRepository: GroupRepository,
    private val friendRepository: FriendsDirectoryRepository
): ViewModel() {

    private val _searchEmailResult = MutableLiveData<Result<UsersEntity>>()
    val searchEmailResult: LiveData<Result<UsersEntity>> = _searchEmailResult

    private val _newGroupResult = MutableLiveData<Result<Boolean>>()
    val newGroupResult: LiveData<Result<Boolean>> = _newGroupResult

    fun findUser(email: String) {
        viewModelScope.launch {
            _searchEmailResult.value = friendRepository.findUser(email)
        }
    }

    suspend fun getAllGroups() = groupRepository.getAllGroups()

    fun resetFoundFriend() {
        _searchEmailResult.value = Result.failure(Exception(Utils.USER_NOT_FOUND))
    }

    fun newGroup(name: String, path: String?, members: List<UsersEntity>, byteArray: ByteArray?) {
        viewModelScope.launch {
            _newGroupResult.value = groupRepository.newGroup(name, path, members, byteArray)
        }
    }
}