package com.penny.planner.data

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class EmailVerificationRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth
) : EmailVerificationRepository {
    override suspend fun sendVerificationEmail() : Result<Boolean> {
        if (auth.currentUser == null) {
            return Result.failure(Exception("User not found"))
        }
        return suspendCoroutine { continuation ->
            auth.currentUser!!.sendEmailVerification()
                .addOnSuccessListener {
                continuation.resume(Result.success(true))
            }.addOnFailureListener {
                    continuation.resume(Result.failure(it))
                }
        }
    }

    override suspend fun isEmailVerified(): Result<Boolean> {
        return try {
            auth.currentUser?.reload()?.await()
            if (auth.currentUser?.isEmailVerified == true) {
                Result.success(true)
            } else
                throw Exception("failed")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}