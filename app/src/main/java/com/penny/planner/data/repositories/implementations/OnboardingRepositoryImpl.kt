package com.penny.planner.data.repositories.implementations

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.penny.planner.data.repositories.interfaces.CategoryAndEmojiRepository
import com.penny.planner.data.repositories.interfaces.OnboardingRepository
import com.penny.planner.helpers.Utils
import com.penny.planner.models.LoginResultModel
import com.penny.planner.models.UserModel
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class OnboardingRepositoryImpl @Inject constructor() : OnboardingRepository {

    @Inject lateinit var categoryAndEmojiRepository: CategoryAndEmojiRepository

    private val auth = FirebaseAuth.getInstance()
    private val directoryReference = FirebaseDatabase.getInstance().getReference(Utils.USERS)
    private val storage = FirebaseStorage.getInstance()

    override suspend fun login(email: String, password: String): Result<LoginResultModel> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val user = result.user ?: throw Exception(Utils.FAILED)
            categoryAndEmojiRepository.checkServerAndUpdateCategory()
            Result.success(LoginResultModel(user.isEmailVerified, !user.displayName.isNullOrBlank()))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signup(email: String, password: String): Result<Boolean> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user ?: throw Exception(Utils.FAILED)
            user.sendEmailVerification()
            categoryAndEmojiRepository.checkServerAndUpdateCategory()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateProfile(name: String, byteArray: ByteArray?): Result<Boolean> {
        return try {
            if (auth.currentUser == null)
                throw Exception(Utils.FAILED)
            val email = auth.currentUser?.email ?: ""
            val id = auth.currentUser?.uid ?: ""
            var downloadPath: Uri? = null
            if (byteArray != null) {
                val storageRef = storage.getReference(Utils.USER_IMAGE).child(id)
                downloadPath = storageRef
                    .putBytes(byteArray)
                    .await()
                    .storage
                    .downloadUrl
                    .await()
            }
            val profileUpdate = UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .setPhotoUri(downloadPath)
                .build()
            auth.currentUser?.updateProfile(profileUpdate)?.await()
            directoryReference.child(Utils.formatEmailForFirebase(email)).child(Utils.USER_INFO).setValue(
                UserModel(
                    email,
                    name,
                    downloadPath?.toString() ?: "", id
                )
            ).await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun sendVerificationEmail() : Result<Boolean> {
        if (auth.currentUser == null)
            return Result.failure(Exception(Utils.USER_NOT_FOUND))
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
                throw Exception(Utils.FAILED)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun sendPasswordResetEmail(email: String) : Result<Boolean> {
        return suspendCoroutine {  continuation ->
            auth.sendPasswordResetEmail(email).addOnSuccessListener {
                continuation.resume(Result.success(true))
            }.addOnFailureListener {
                continuation.resume(Result.failure(it))
            }
        }
    }

    override fun getEmailId(): String {
        return auth.currentUser?.email ?: ""
    }

}