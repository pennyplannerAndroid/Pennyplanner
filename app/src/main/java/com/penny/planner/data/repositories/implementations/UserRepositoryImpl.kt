package com.penny.planner.data.repositories.implementations

import android.app.Application
import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.penny.planner.data.db.friends.UsersEntity
import com.penny.planner.data.repositories.interfaces.DatabaseRepository
import com.penny.planner.data.repositories.interfaces.FriendsDirectoryRepository
import com.penny.planner.data.repositories.interfaces.UserRepository
import com.penny.planner.helpers.Utils
import kotlinx.coroutines.tasks.await
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val friendsRepository: FriendsDirectoryRepository,
    private val dbRepository: DatabaseRepository
): UserRepository {

    @Inject lateinit var applicationContext: Application

    private val auth = FirebaseAuth.getInstance()
    private val directoryReference = FirebaseDatabase.getInstance().getReference(Utils.USERS)
    private val storage = FirebaseStorage.getInstance()

    override fun navigationToOnboardingNeeded(): String? {
        if (auth.currentUser == null)
            return Utils.TUTORIAL
        else if (!auth.currentUser?.isEmailVerified!!)
            return Utils.EMAIL_VERIFICATION
        else if (auth.currentUser?.displayName == null || FirebaseAuth.getInstance().currentUser?.displayName!!.isEmpty()) {
            return Utils.UPDATE_PROFILE
        }
        return null
    }

    override fun getUserName(): String {
        return auth.currentUser?.displayName ?: Utils.USER
    }

    override fun getEmail(): String {
        return auth.currentUser?.email ?: Utils.DEFAULT_EMAIL_STRING
    }

    override suspend fun getImagePath(): String {
        return friendsRepository.findFriend(getEmail()).localImagePath.ifEmpty {
            FirebaseAuth.getInstance().currentUser?.photoUrl.toString()
        }
    }

    override fun getSelfId() = FirebaseAuth.getInstance().currentUser!!.uid

    override suspend fun getSelfProfile(): UsersEntity {
        return friendsRepository.findFriend(auth.currentUser!!.email!!)
    }

    override fun logout() {
        auth.signOut()
        dbRepository.deleteDb()
    }

    override suspend fun updateProfile(
        name: String,
        byteArray: ByteArray?,
        isNameChanged: Boolean,
        isImageChanged: Boolean
    ): Result<Boolean> {
        return try {
            if (auth.currentUser == null)
                throw Exception(Utils.FAILED)
            val email = auth.currentUser?.email ?: ""
            val id = auth.currentUser?.uid ?: ""
            if (isImageChanged) {
                var localImagePath = ""
                var downloadPath: Uri? = null
                if (byteArray != null) {
                    localImagePath = saveImageLocally(byteArray, id)
                    val storageRef = storage.getReference(Utils.USER_IMAGE).child(id)
                    downloadPath = storageRef
                        .putBytes(byteArray)
                        .await()
                        .storage
                        .downloadUrl
                        .await()
                }
                val profileUpdate = if (isNameChanged)
                    UserProfileChangeRequest.Builder()
                        .setDisplayName(name)
                        .setPhotoUri(downloadPath)
                        .build()
                else
                    UserProfileChangeRequest.Builder()
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
                auth.currentUser?.let {
                    val self = UsersEntity(
                        id = auth.currentUser!!.uid,
                        name = auth.currentUser!!.displayName!!,
                        profileImageURL = auth.currentUser!!.photoUrl?.toString() ?: "",
                        email = auth.currentUser!!.email!!,
                        localImagePath = localImagePath
                    )
                    friendsRepository.updateFriend(self)
                }
            } else {
                val profileUpdate = UserProfileChangeRequest.Builder()
                    .setDisplayName(name)
                    .build()
                auth.currentUser?.updateProfile(profileUpdate)?.await()
                directoryReference
                    .child(Utils.formatEmailForFirebase(email))
                    .child(Utils.USER_INFO).child(Utils.NAME)
                    .setValue(name)
                    .await()
                friendsRepository.updateName(name, id)
            }
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
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