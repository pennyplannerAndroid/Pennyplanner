package com.penny.planner.data

interface EmailVerificationRepository {
    suspend fun sendVerificationEmail() : Result<Boolean>
    suspend fun isEmailVerified() : Result<Boolean>
}