package com.penny.planner.data.repositories.implementations

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.penny.planner.data.db.groups.GroupDao
import com.penny.planner.data.db.groups.GroupEntity
import com.penny.planner.data.repositories.interfaces.GroupRepository
import com.penny.planner.helpers.Utils
import com.penny.planner.models.GroupFireBaseModel
import com.penny.planner.models.UserModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class GroupRepositoryImpl @Inject constructor(
    private val groupDao: GroupDao
): GroupRepository {

    val scope = CoroutineScope(Job() + Dispatchers.IO)
    private val auth = FirebaseAuth.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val userDirectory = FirebaseDatabase.getInstance().getReference(Utils.USERS)

    override fun getAllGroupsFromFirebase() {
        if (auth.currentUser != null) {
            scope.launch {
                fetchAllGroupsFromFirebase(Utils.JOINED)
            }
        }
    }

    override fun getAllPendingGroups() {
        if (auth.currentUser != null) {
            scope.launch {
                fetchAllGroupsFromFirebase(Utils.PENDING)
            }
        }
    }

    private fun fetchAllGroupsFromFirebase(source: String) {
        userDirectory
            .child(Utils.formatEmailForFirebase(auth.currentUser!!.email!!))
            .child(Utils.GROUP_INFO)
            .child(source).addValueEventListener(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val pendingGroups = snapshot.value as Map<*, *>
                        if (pendingGroups.isNotEmpty()) {
                            for (groupId in pendingGroups.keys) {
                                updateLocalWithPendingGroups(groupId.toString(), source == Utils.PENDING)
                            }
                        }
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    Log.d("GroupRepository:: ", error.message)
                }
            })
    }

    private fun updateLocalWithPendingGroups(groupId: String, needUpdatePendingList: Boolean) {
        FirebaseDatabase.getInstance()
            .getReference(Utils.GROUPS)
            .child(groupId).addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val entity = snapshot.getValue(GroupEntity::class.java)
                        if (entity != null) {
                            scope.launch {
                                groupDao.addGroup(entity)
                                if (needUpdatePendingList)
                                    updatePendingNode(entity)
                            }
                        }
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    Log.d("GroupRepository:: ", error.message)
                }
            })
    }

    private fun updatePendingNode(entity: GroupEntity) {
        userDirectory
            .child(Utils.formatEmailForFirebase(auth.currentUser!!.email!!))
            .child(Utils.GROUP_INFO)
            .child(Utils.JOINED).child(entity.groupId).setValue(Utils.NON_ADMIN_VALUE)
        userDirectory
            .child(Utils.formatEmailForFirebase(auth.currentUser!!.email!!))
            .child(Utils.GROUP_INFO)
            .child(Utils.PENDING).child(entity.groupId).removeValue()
    }

    override suspend fun getAllGroups(): LiveData<List<GroupEntity>> {
        return groupDao.getAllGroups()
    }

    override suspend fun newGroup(name: String, path: String?, members: List<String>, byteArray: ByteArray?): Result<Boolean> {
        if (auth.currentUser != null) {
            val groupId = "${auth.currentUser!!.uid}${System.currentTimeMillis()}"
            val groupEntity = GroupEntity(
                groupId = groupId,
                name = name,
                members = members.plus(auth.currentUser!!.email!!),
                profileUrl = "",
                creatorId = auth.currentUser!!.email!!
            )
            FirebaseDatabase.getInstance()
                .getReference(Utils.GROUPS)
                .child(groupId)
                .setValue(
                    GroupFireBaseModel(
                    groupId = groupId,
                    name = name,
                    members = groupEntity.members,
                    creatorId = groupEntity.creatorId!!
                    )
                ).await()
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
                    .child(Utils.PROFILE_URL).setValue(downloadPath.toString())
            }
            userDirectory
                .child(Utils.formatEmailForFirebase(auth.currentUser!!.email!!))
                .child(Utils.GROUP_INFO)
                .child(Utils.JOINED).child(groupId).setValue(Utils.ADMIN_VALUE)
            for (user in groupEntity.members) {
                if (user.isNotEmpty()) {
                    userDirectory
                        .child(Utils.formatEmailForFirebase(user))
                        .child(Utils.GROUP_INFO)
                        .child(Utils.PENDING).child(groupId).setValue(Utils.NON_ADMIN_VALUE)
                }
            }
            groupEntity.profileUrl = path
            groupDao.addGroup(entity = groupEntity)
            return Result.success(true)
        } else {
            auth.signOut()
            return Result.failure(Exception(Utils.SESSION_EXPIRED_ERROR))
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