package com.penny.planner.data

interface SignUpRepository {
    suspend fun signup(email: String, password: String) : Result<Boolean>
}