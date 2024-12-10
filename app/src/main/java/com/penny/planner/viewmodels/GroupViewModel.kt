package com.penny.planner.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.penny.planner.data.repositories.interfaces.GroupRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GroupViewModel @Inject constructor(
    private val groupRepository: GroupRepository
): ViewModel() {

    private val _newGroupResult = MutableLiveData<Result<Boolean>>()
    val newGroupResult: LiveData<Result<Boolean>> = _newGroupResult

    suspend fun getAllGroups() = groupRepository.getAllGroupLists()

    fun newGroup(name: String,
                 path: String?,
                 monthlyBudget: Double,
                 safeToSpendLimit: Int,
                 byteArray: ByteArray?
    ) {
        viewModelScope.launch {
            _newGroupResult.value = groupRepository.newGroup(name, path, monthlyBudget, safeToSpendLimit, byteArray)
        }
    }

    fun isAdmin(creatorId: String) = groupRepository.isAdmin(creatorId)
}