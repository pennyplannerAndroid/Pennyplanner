package com.penny.planner.data.repositories.interfaces

interface UserRepository {
    fun navigationToOnboardingNeeded(): String?
    fun getUserName() : String
    fun getEmail() : String
    suspend fun getImagePath() : String
    fun getSelfId() : String
}