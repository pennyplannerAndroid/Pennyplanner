package com.penny.planner.data

import com.google.firebase.auth.FirebaseAuth
import com.penny.planner.models.FirebaseUser
import com.penny.planner.models.LoginResultModel
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class LoginRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth
) : LoginRepository {
    override suspend fun login(firebaseUser: FirebaseUser): Result<LoginResultModel> {
        return try {
            val result = auth.signInWithEmailAndPassword(firebaseUser.email, firebaseUser.password).await()
            val user = result.user ?: throw Exception("Failed")
            Result.success(LoginResultModel(user.isEmailVerified))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}