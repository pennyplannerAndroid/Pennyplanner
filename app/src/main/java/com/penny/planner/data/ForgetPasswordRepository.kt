package com.penny.planner.data

interface ForgetPasswordRepository {
    suspend fun sendPasswordResetEmail(email: String) : Result<Boolean>
}