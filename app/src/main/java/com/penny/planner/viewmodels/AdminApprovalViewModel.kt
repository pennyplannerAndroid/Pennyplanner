package com.penny.planner.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.penny.planner.data.db.friends.UsersEntity
import com.penny.planner.data.db.groups.GroupEntity
import com.penny.planner.data.repositories.interfaces.GroupRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AdminApprovalViewModel @Inject constructor(
    private val groupRepository: GroupRepository
) : ViewModel() {

    private var groupId = ""
    private val _approvalList = MutableLiveData<Result<List<UsersEntity>>>()
    val approvalList: LiveData<Result<List<UsersEntity>>> = _approvalList

    fun setGroupId(groupId: String) {
        this.groupId = groupId
    }

    suspend fun getGroup() = groupRepository.getGroupById(groupId)

    fun getApprovalList() {
        viewModelScope.launch(Dispatchers.IO) {
            val result = groupRepository.getApprovalList(groupId)
            withContext(Dispatchers.Main) {
                _approvalList.value = result
            }
        }
    }

    fun rejectAll() {
        viewModelScope.launch(Dispatchers.IO) {
            val result = groupRepository.rejectAll(groupId)
            withContext(Dispatchers.Main) {
                if (result.isSuccess && result.getOrNull()!!) {
                    _approvalList.value = Result.success(mutableListOf())
                }
            }
        }
    }

    fun accept(group: GroupEntity, user: UsersEntity, needUpdatePendingFlag: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = groupRepository.approveToJoin(group = group, usersEntity = user, needUpdatePendingFlag = needUpdatePendingFlag)
            withContext(Dispatchers.Main) {
                if (result.isSuccess && result.getOrNull()!!) {
                    val list = _approvalList.value?.getOrNull()?.toMutableList()
                    if (list!= null) {
                        list.remove(user)
                        _approvalList.value = Result.success(list.toList())
                    }
                }
            }
        }
    }

    fun reject(group: GroupEntity, user: UsersEntity, needUpdatePendingFlag: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = groupRepository.rejectRequestToJoin(group = group, usersEntity = user, needUpdatePendingFlag = needUpdatePendingFlag)
            withContext(Dispatchers.Main) {
                if (result.isSuccess && result.getOrNull()!!) {
                    val list = _approvalList.value?.getOrNull()?.toMutableList()
                    if (list!= null) {
                        list.remove(user)
                        _approvalList.value = Result.success(list.toList())
                    }
                }
            }
        }
    }

}