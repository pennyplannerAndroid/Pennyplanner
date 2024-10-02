package com.penny.planner.data.repositories.interfaces

import androidx.lifecycle.LiveData
import com.penny.planner.data.db.groups.GroupEntity
import com.penny.planner.models.UserModel

interface GroupRepository {
    fun getAllPendingGroups()
    suspend fun getAllGroups(): LiveData<List<GroupEntity>>
    suspend fun newGroup(name: String, path: String?, members: List<String>, byteArray: ByteArray?): Result<Boolean>
    suspend fun findUser(email: String): Result<UserModel>
}