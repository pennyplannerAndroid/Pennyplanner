package com.penny.planner.data.repositories.interfaces

import com.penny.planner.data.db.friends.UsersEntity

interface UserRepository {
    fun navigationToOnboardingNeeded(): String?
    fun getUserName() : String
    fun getEmail() : String
    suspend fun getImagePath() : String
    fun getSelfId() : String
    suspend fun getSelfProfile() : UsersEntity
    suspend fun updateProfile(name: String, byteArray: ByteArray?, isNameChanged: Boolean, isImageChanged: Boolean) : Result<Boolean>
    fun logout()
}