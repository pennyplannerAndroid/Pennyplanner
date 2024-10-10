package com.penny.planner.data.repositories.interfaces

import androidx.lifecycle.LiveData
import com.penny.planner.data.db.groups.GroupEntity

interface GroupRepository {
    suspend fun getAllGroups(): LiveData<List<GroupEntity>>
    suspend fun getGroupById(groupId: String): GroupEntity
    suspend fun newGroup(name: String, path: String?, members: List<String>, byteArray: ByteArray?): Result<Boolean>
}