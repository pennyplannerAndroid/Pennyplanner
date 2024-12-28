package com.penny.planner.data.repositories.interfaces

import com.penny.planner.data.db.friends.UsersEntity

interface FriendsDirectoryRepository {
    suspend fun findUserFromServer(email: String): Result<UsersEntity>
    suspend fun addFriend(entity: UsersEntity)
    suspend fun addFriend(list: List<UsersEntity>)
    suspend fun updateFriend(entity: UsersEntity)
    suspend fun getFriends(list: List<String>): List<UsersEntity>
    suspend fun doesFriendExists(email: String): Boolean
    suspend fun getAllFriends(): List<UsersEntity>
    suspend fun findFriend(email: String): UsersEntity
}