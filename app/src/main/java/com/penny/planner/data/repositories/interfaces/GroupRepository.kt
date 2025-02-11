package com.penny.planner.data.repositories.interfaces

import androidx.lifecycle.LiveData
import com.penny.planner.data.db.friends.UsersEntity
import com.penny.planner.data.db.groups.GroupEntity
import com.penny.planner.models.GroupListDisplayModel

interface GroupRepository {
    suspend fun getAllGroupLists(): LiveData<List<GroupListDisplayModel>>
    suspend fun getGroupById(groupId: String): GroupEntity
    suspend fun newGroup(name: String, path: String?, monthlyBudget: Double, safeToSpendLimit: Int, byteArray: ByteArray?): Result<Boolean>
    suspend fun addGroup(groupEntity: GroupEntity)
    suspend fun updateGroupMembers(group: GroupEntity)
    fun isAdmin(creatorId: String): Boolean
    suspend fun searchGroup(groupId: String): Result<GroupEntity>
    suspend fun joinExistingGroup(group: GroupEntity): Result<Boolean>
    suspend fun getApprovalList(groupId: String): Result<List<UsersEntity>>
    suspend fun approveToJoin(group: GroupEntity, usersEntity: UsersEntity, needUpdatePendingFlag: Boolean): Result<Boolean>
    suspend fun rejectRequestToJoin(group: GroupEntity, usersEntity: UsersEntity, needUpdatePendingFlag: Boolean): Result<Boolean>
    suspend fun rejectAll(groupId: String): Result<Boolean>
    suspend fun checkUpdateOfPendingGroups()
}