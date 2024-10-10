package com.penny.planner.data.repositories.interfaces

import com.penny.planner.models.UserModel

interface FriendsDirectoryRepository {
    suspend fun findUser(email: String): Result<UserModel>
}