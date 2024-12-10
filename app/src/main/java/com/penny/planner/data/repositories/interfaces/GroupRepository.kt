package com.penny.planner.data.repositories.interfaces

import androidx.lifecycle.LiveData
import com.penny.planner.data.db.groups.GroupEntity
import com.penny.planner.models.GroupListDisplayModel

interface GroupRepository {
    suspend fun getAllGroupLists(): LiveData<List<GroupListDisplayModel>>
    suspend fun getGroupById(groupId: String): GroupEntity
    suspend fun newGroup(name: String, path: String?, monthlyBudget: Double, safeToSpendLimit: Int, byteArray: ByteArray?): Result<Boolean>
    suspend fun addGroup(groupEntity: GroupEntity)
    fun isAdmin(creatorId: String): Boolean
}