package com.penny.planner.data.repositories.interfaces

interface UserRepository {
    fun navigationToOnboardingNeeded(): String?
    fun getUserName() : String
    fun getEmail() : String
    fun getImagePath() : String
    fun getSelfId() : String
}