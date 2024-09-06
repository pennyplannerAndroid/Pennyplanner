package com.penny.planner.data.repositories

import com.penny.planner.models.GroupModel
import com.penny.planner.models.UserModel

interface UserAndExpenseRepository {
    suspend fun newGroup(group: GroupModel, byteArray: ByteArray?): Result<Boolean>
    fun getUserName() : String
    fun getEmail() : String
    suspend fun findUser(email: String): Result<UserModel>
}