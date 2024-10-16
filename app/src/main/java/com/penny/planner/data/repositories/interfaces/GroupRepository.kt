package com.penny.planner.data.repositories.interfaces

import androidx.lifecycle.LiveData
import androidx.room.Update
import com.penny.planner.data.db.friends.UsersEntity
import com.penny.planner.data.db.groups.GroupEntity

interface GroupRepository {
    suspend fun getAllGroups(): LiveData<List<GroupEntity>>
    suspend fun getGroupById(groupId: String): GroupEntity
    suspend fun newGroup(name: String, path: String?, members: List<UsersEntity>, byteArray: ByteArray?): Result<Boolean>
    suspend fun addGroup(groupEntity: GroupEntity)
    suspend fun getAllExistingGroupsFromDb(): List<GroupEntity>    @Update
    suspend fun updateEntity(entity: GroupEntity)
}