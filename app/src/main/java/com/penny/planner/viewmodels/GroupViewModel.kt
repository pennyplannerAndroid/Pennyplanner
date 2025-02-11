package com.penny.planner.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.penny.planner.data.db.friends.UsersEntity
import com.penny.planner.data.db.groups.GroupEntity
import com.penny.planner.data.repositories.interfaces.GroupRepository
import com.penny.planner.data.repositories.interfaces.ProfilePictureRepository
import com.penny.planner.helpers.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class GroupViewModel @Inject constructor(
    private val groupRepository: GroupRepository,
    private val profilePictureRepository: ProfilePictureRepository
): ViewModel() {

    private val _newGroupResult = MutableLiveData<Result<Boolean>>()
    val newGroupResult: LiveData<Result<Boolean>> = _newGroupResult

    private val _searchGroupResult = MutableLiveData<Result<GroupEntity>?>()
    val searchGroupResult: LiveData<Result<GroupEntity>?> = _searchGroupResult

    private val _joinExistingGroup = MutableLiveData<Result<Boolean>?>()
    val joinExistingGroup: LiveData<Result<Boolean>?> = _joinExistingGroup

    var deepLinkGroupId = ""
        get() = field
        set(value) {
            field = value
        }

    suspend fun getAllGroups() = groupRepository.getAllGroupLists()

    fun newGroup(name: String,
                 path: String?,
                 monthlyBudget: Double,
                 safeToSpendLimit: Int,
                 byteArray: ByteArray?
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = groupRepository.newGroup(name, path, monthlyBudget, safeToSpendLimit, byteArray)
            withContext(Dispatchers.Main) {
                _newGroupResult.value = result
            }
        }
    }

    fun isAdmin(creatorId: String) = groupRepository.isAdmin(creatorId)

    fun getTwoFriends(list: List<String>): List<UsersEntity> {
        val result = mutableListOf<UsersEntity>()
        result.add(profilePictureRepository.findLocalImagePath(list[0]))
        if (list.size > 1) {
            result.add(profilePictureRepository.findLocalImagePath(list[1]))
        }
        return result
    }

    fun getJoinGroupLink(groupId: String): String {
        return "${Utils.BASE_URL}${Utils.JOIN_GROUP_QUERY}$groupId"
    }

    fun searchGroup(groupId: String = deepLinkGroupId) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = groupRepository.searchGroup(groupId)
            withContext(Dispatchers.Main) {
                _searchGroupResult.value = result
            }
        }
    }

    fun joinGroup(group: GroupEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = groupRepository.joinExistingGroup(group)
            withContext(Dispatchers.Main) {
                _joinExistingGroup.value = result
            }
        }
    }

    fun resetDeeplinkSearch() {
        _searchGroupResult.value = null
        _joinExistingGroup.value = null
        deepLinkGroupId = ""
    }

    fun checkUpdateOfPendingGroups() {
        viewModelScope.launch(Dispatchers.IO) {
            groupRepository.checkUpdateOfPendingGroups()

        }
    }
}