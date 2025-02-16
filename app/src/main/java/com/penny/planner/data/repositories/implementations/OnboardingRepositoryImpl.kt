package com.penny.planner.data.repositories.implementations

import android.app.Application
import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.penny.planner.data.db.friends.UsersEntity
import com.penny.planner.data.repositories.interfaces.CategoryAndEmojiRepository
import com.penny.planner.data.repositories.interfaces.FirebaseBackgroundSyncRepository
import com.penny.planner.data.repositories.interfaces.FriendsDirectoryRepository
import com.penny.planner.data.repositories.interfaces.MonthlyBudgetRepository
import com.penny.planner.data.repositories.interfaces.OnboardingRepository
import com.penny.planner.data.repositories.interfaces.ProfilePictureRepository
import com.penny.planner.helpers.Utils
import com.penny.planner.helpers.enums.LoginResult
import com.penny.planner.models.MonthlyBudgetInfoModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class OnboardingRepositoryImpl @Inject constructor() : OnboardingRepository {

    @Inject lateinit var applicationScope: CoroutineScope
    @Inject lateinit var categoryAndEmojiRepository: CategoryAndEmojiRepository
    @Inject lateinit var firebaseBackgroundRepository: FirebaseBackgroundSyncRepository
    @Inject lateinit var budgetRepository: MonthlyBudgetRepository
    @Inject lateinit var usersRepository: FriendsDirectoryRepository
    @Inject lateinit var profilePictureRepository: ProfilePictureRepository
    @Inject lateinit var applicationContext: Application

    private val auth = FirebaseAuth.getInstance()
    private val directoryReference = FirebaseDatabase.getInstance().getReference(Utils.USERS)
    private val storage = FirebaseStorage.getInstance()

    override suspend fun login(email: String, password: String): Result<LoginResult> {
        try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val user = result.user ?: throw Exception(Utils.FAILED)
            if (!user.isEmailVerified)
                return Result.success(LoginResult.VERIFY_EMAIL)
            else if (user.displayName.isNullOrBlank()) {
                Result.success(LoginResult.ADD_NAME)
            }
            val snapshot = directoryReference.child(Utils.formatEmailForFirebase(email))
                .child(Utils.BUDGET_INFO).get().await()
            if (snapshot.exists()) {
                val entity = snapshot.getValue(MonthlyBudgetInfoModel::class.java)
                if (entity != null) {
                    applicationScope.launch {
                        budgetRepository.updateLocalWithMonthlyBudget(entity)
                        getAllFirebaseDataAndUpdateLocal(isLogin = true)
                    }
                    return Result.success(LoginResult.VERIFY_SUCCESS)
                } else {
                    getAllFirebaseDataAndUpdateLocal(isLogin = true, isFirstTime = false)
                    return Result.success(LoginResult.ADD_BUDGET)
                }
            } else {
                getAllFirebaseDataAndUpdateLocal(isLogin = true, isFirstTime = false)
                return Result.success(LoginResult.ADD_BUDGET)
            }
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }

    private fun getAllFirebaseDataAndUpdateLocal(isLogin: Boolean = false, localImagePath: String = "", isFirstTime: Boolean = true) {
        applicationScope.launch {
            auth.currentUser?.let {
                val self = UsersEntity(
                    id = auth.currentUser!!.uid,
                    name = auth.currentUser!!.displayName!!,
                    profileImageURL = auth.currentUser!!.photoUrl?.toString() ?: "",
                    email = auth.currentUser!!.email!!,
                    localImagePath = localImagePath
                )
                usersRepository.addFriend(self) // adding self to the friend table
                if (isLogin)
                    profilePictureRepository.downloadProfilePicture(self)
            }
            categoryAndEmojiRepository.checkServerAndUpdateCategory()
            if (isLogin && isFirstTime) {
                firebaseBackgroundRepository.getAllPersonalExpenses()
                firebaseBackgroundRepository.getAllJoinedGroupsFromFirebaseAtLogin()
            }
        }
    }

    override suspend fun signup(email: String, password: String): Result<Boolean> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user ?: throw Exception(Utils.FAILED)
            user.sendEmailVerification()
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
            var localImagePath = ""
            if (byteArray != null) {
                localImagePath = saveImageLocally(byteArray, id)
                usersRepository.updateFriend(UsersEntity(id, email, name))
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
                UsersEntity(
                    id = id,
                    email = email,
                    name = name,
                    profileImageURL = downloadPath?.toString() ?: ""
                )
            ).await()
            getAllFirebaseDataAndUpdateLocal(localImagePath = localImagePath)
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

    override fun getName(): String {
        return auth.currentUser?.displayName ?: ""
    }

    private fun saveImageLocally(byteArray: ByteArray, id: String) : String {
        return try {
            val file = File(applicationContext.filesDir, "${id}.jpeg")
            FileOutputStream(file).use { fos ->
                fos.write(byteArray)
            }
            file.absolutePath
        } catch (e: Exception) {
            ""
        }
    }
}