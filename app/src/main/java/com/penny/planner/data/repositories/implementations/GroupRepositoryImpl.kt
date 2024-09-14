package com.penny.planner.data.repositories.implementations

import android.net.Uri
import androidx.lifecycle.LiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.penny.planner.data.repositories.interfaces.GroupRepository
import com.penny.planner.helpers.Utils
import com.penny.planner.models.GroupModel
import com.penny.planner.models.UserModel
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class GroupRepositoryImpl @Inject constructor(): GroupRepository {

    private val auth = FirebaseAuth.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val userDirectory = FirebaseDatabase.getInstance().getReference(Utils.USERS)

    override suspend fun getAllGroups(): LiveData<List<GroupModel>> {
        TODO("Not yet implemented")
    }

    override suspend fun newGroup(group: GroupModel, byteArray: ByteArray?): Result<Boolean> {
        if (auth.currentUser != null) {
            val groupId = "${auth.currentUser!!.uid}${System.currentTimeMillis()}"
            FirebaseDatabase.getInstance()
                .getReference(Utils.GROUPS)
                .child(groupId)
                .setValue(GroupModel(
                    name = group.name,
                    members = group.members?.plus(auth.currentUser!!.email!!),
                    id = groupId
                )).await()
            var downloadPath: Uri? = null
            if (byteArray != null) {
                val storageRef = storage.getReference(Utils.USER_IMAGE).child(groupId)
                downloadPath = storageRef
                    .putBytes(byteArray)
                    .await()
                    .storage
                    .downloadUrl
                    .await()
            }
            if (downloadPath != null) {
                FirebaseDatabase.getInstance()
                    .getReference(Utils.GROUPS)
                    .child(groupId)
                    .child("profileUrl").setValue(downloadPath.toString())
            }
            userDirectory
                .child(Utils.formatEmailForFirebase(auth.currentUser!!.email!!))
                .child(Utils.GROUPS)
                .child(Utils.JOINED).child(groupId).setValue("1")
            for (user in group.members!!) {
                if (user.isNotEmpty()) {
                    userDirectory
                        .child(Utils.formatEmailForFirebase(user))
                        .child(Utils.GROUPS)
                        .child(Utils.PENDING).child(groupId).setValue("0")
                }
            }
            return Result.success(true)
        } else {
            auth.signOut()
            return Result.failure(Exception("Session expired! Login again."))
        }
    }

    override suspend fun findUser(email: String): Result<UserModel> {
        if (email == auth.currentUser?.email)
            return Result.failure(Exception(Utils.SAME_EMAIL_ERROR))
        return suspendCoroutine { continuation ->
            try {
                userDirectory.child(Utils.formatEmailForFirebase(email)).child(Utils.USER_INFO).addValueEventListener(object: ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            val model = snapshot.getValue(UserModel::class.java) as UserModel
                            continuation.resume(Result.success(model))
                        } else
                            continuation.resume(Result.failure(Exception(Utils.USER_NOT_FOUND)))
                    }
                    override fun onCancelled(error: DatabaseError) {
                        continuation.resume(Result.failure(Exception(error.message)))
                    }
                })
            } catch (error: Exception) {
                continuation.resume(Result.failure(error))
            }
        }
    }
}