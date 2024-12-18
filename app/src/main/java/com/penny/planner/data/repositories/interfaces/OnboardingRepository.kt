package com.penny.planner.data.repositories.interfaces

import com.penny.planner.helpers.enums.LoginResult

interface OnboardingRepository {
    suspend fun login(email: String, password: String) : Result<LoginResult>
    suspend fun updateProfile(name: String, byteArray: ByteArray?) : Result<Boolean>
    suspend fun sendVerificationEmail() : Result<Boolean>
    suspend fun isEmailVerified() : Result<Boolean>
    suspend fun sendPasswordResetEmail(email: String) : Result<Boolean>
    suspend fun signup(email: String, password: String) : Result<Boolean>
    fun getEmailId() : String
    fun getName(): String
}