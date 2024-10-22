package com.penny.planner.data.repositories.interfaces

interface UserRepository {
    fun isLoggedIn(): Boolean
    fun navigateToOnBoardingScreen(): String
    fun getUserName() : String
    fun getEmail() : String
    fun getImagePath() : String
    fun getSelfId() : String
}