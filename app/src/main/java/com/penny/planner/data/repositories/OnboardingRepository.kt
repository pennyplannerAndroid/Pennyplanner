package com.penny.planner.data.repositories

import com.penny.planner.models.LoginResultModel

interface OnboardingRepository {
    suspend fun login(email: String, password: String) : Result<LoginResultModel>
    suspend fun updateProfile(name: String, byteArray: ByteArray?) : Result<Boolean>
    suspend fun sendVerificationEmail() : Result<Boolean>
    suspend fun isEmailVerified() : Result<Boolean>
    suspend fun sendPasswordResetEmail(email: String) : Result<Boolean>
    suspend fun signup(email: String, password: String) : Result<Boolean>
    fun getEmailId() : String
}