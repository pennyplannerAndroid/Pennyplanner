package com.penny.planner.data

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class SignUpRepositoryImpl @Inject constructor(
    private val mAuth: FirebaseAuth
) : SignUpRepository {

    override suspend fun signup(email: String, password: String): Result<Boolean> {
        return try {
            val result = mAuth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user ?: throw Exception("Failed")
            user.sendEmailVerification()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}