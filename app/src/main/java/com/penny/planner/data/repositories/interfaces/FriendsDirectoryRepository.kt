package com.penny.planner.data.repositories.interfaces

import com.penny.planner.data.db.friends.UsersEntity

interface FriendsDirectoryRepository {
    suspend fun findUser(email: String): Result<UsersEntity>
    suspend fun addFriend(entity: UsersEntity)
    suspend fun downloadProfilePicture(entity: UsersEntity)
    suspend fun addFriend(list: List<UsersEntity>)
    suspend fun updateFriend(entity: UsersEntity)
    suspend fun getFriends(list: List<String>): List<UsersEntity>
    suspend fun doesFriendExists(email: String): Boolean
}