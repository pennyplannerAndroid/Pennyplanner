package com.penny.planner.data.repositories.interfaces

import com.penny.planner.data.db.friends.UsersEntity

interface ProfilePictureRepository {
    fun initialize()
    suspend fun downloadProfilePicture(entity: UsersEntity)
    fun findLocalImagePath(email: String): UsersEntity
}