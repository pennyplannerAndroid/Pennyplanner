package com.penny.planner.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.penny.planner.data.db.expense.ExpenseEntity
import com.penny.planner.data.db.friends.UsersEntity
import com.penny.planner.data.db.groups.GroupEntity
import com.penny.planner.data.repositories.interfaces.ExpenseRepository
import com.penny.planner.data.repositories.interfaces.GroupRepository
import com.penny.planner.data.repositories.interfaces.ProfilePictureRepository
import com.penny.planner.data.repositories.interfaces.UserRepository
import com.penny.planner.helpers.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class GroupSessionViewModel @Inject constructor(
    private val expenseRepository: ExpenseRepository,
    private val groupRepository: GroupRepository,
    private val useProfilePictureRepository: ProfilePictureRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private var groupId = ""
    private val _approvalList = MutableLiveData<Boolean>()
    val approvalList: LiveData<Boolean> = _approvalList

    fun setGroupId(groupId: String) {
        this.groupId = groupId
    }

    suspend fun getGroup() = groupRepository.getGroupById(groupId)

    suspend fun getAllExpenses() = expenseRepository.getAllExpenses(groupId)

    fun addExpense(expense: ExpenseEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            expense.groupId = groupId
            expenseRepository.addExpense(expense)
        }
    }

    suspend fun getMonthlyExpenses() =
        expenseRepository.getMonthlyExpenseEntity(groupId, Utils.getCurrentMonthYear())

    fun addMessage(message: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val expense = ExpenseEntity(
                content = message,
                entityType = 0,
                groupId = groupId
            )
            expenseRepository.addExpense(expense)
        }
    }

    fun getAllMembers(group: GroupEntity): List<UsersEntity> {
        val members = mutableListOf<UsersEntity>()
        for (email in group.members) {
            members.add(useProfilePictureRepository.findLocalImagePath(email))
        }
        return members
    }

    fun getSelfId() = userRepository.getSelfId()

    fun updateMembers(group: GroupEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            groupRepository.updateGroupMembers(group)
        }
    }

    fun isAdmin(group: GroupEntity) =
        groupRepository.isAdmin(group.creatorId)

    fun getApprovalList() {
        viewModelScope.launch(Dispatchers.IO) {
            val list = groupRepository.getApprovalList(groupId)
            withContext(Dispatchers.Main) {
                var result = false
                if (list.isSuccess) {
                    if ((list.getOrNull()?.size ?: 0) > 0) {
                        result = true
                    }
                }
                _approvalList.value = result
            }
        }
    }

}