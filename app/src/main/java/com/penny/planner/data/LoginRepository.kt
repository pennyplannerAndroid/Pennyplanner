package com.penny.planner.data

import com.penny.planner.models.FirebaseUser
import com.penny.planner.models.LoginResultModel

interface LoginRepository {
    suspend fun login(firebaseUser: FirebaseUser) : Result<LoginResultModel>
}