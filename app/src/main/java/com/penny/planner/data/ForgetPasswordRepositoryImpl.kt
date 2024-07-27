package com.penny.planner.data

import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class ForgetPasswordRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : ForgetPasswordRepository {

    override suspend fun sendPasswordResetEmail(email: String) : Result<Boolean> {
        return suspendCoroutine {  continuation ->
            firebaseAuth.sendPasswordResetEmail(email).addOnSuccessListener {
                continuation.resume(Result.success(true))
            }.addOnFailureListener {
                continuation.resume(Result.failure(it))
            }
        }
    }
}