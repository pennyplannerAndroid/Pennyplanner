package com.penny.planner.data.repositories

interface UserAndExpenseRepository {
    suspend fun newGroup(email: String): Result<Boolean>
    fun getUserName() : String
}