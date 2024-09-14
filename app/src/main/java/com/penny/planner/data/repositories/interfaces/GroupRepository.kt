package com.penny.planner.data.repositories.interfaces

import androidx.lifecycle.LiveData
import com.penny.planner.models.GroupModel
import com.penny.planner.models.UserModel

interface GroupRepository {
    suspend fun getAllGroups(): LiveData<List<GroupModel>>
    suspend fun newGroup(group: GroupModel, byteArray: ByteArray?): Result<Boolean>
    suspend fun findUser(email: String): Result<UserModel>
}