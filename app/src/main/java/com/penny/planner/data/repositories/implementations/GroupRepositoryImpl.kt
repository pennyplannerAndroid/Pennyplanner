package com.penny.planner.data.repositories.implementations

import android.net.Uri
import androidx.lifecycle.LiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.penny.planner.data.db.friends.UsersEntity
import com.penny.planner.data.db.groups.GroupDao
import com.penny.planner.data.db.groups.GroupEntity
import com.penny.planner.data.repositories.interfaces.FirebaseBackgroundSyncRepository
import com.penny.planner.data.repositories.interfaces.FriendsDirectoryRepository
import com.penny.planner.data.repositories.interfaces.GroupRepository
import com.penny.planner.helpers.Utils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class GroupRepositoryImpl @Inject constructor(
    private val groupDao: GroupDao,
    private val firebaseBackgroundSyncRepository: FirebaseBackgroundSyncRepository,
    private val usersRepository: FriendsDirectoryRepository
): GroupRepository {

    val scope = CoroutineScope(Job() + Dispatchers.IO)
    private val auth = FirebaseAuth.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val userDirectory = FirebaseDatabase.getInstance().getReference(Utils.USERS)

    override suspend fun getAllGroups(): LiveData<List<GroupEntity>> {
        return groupDao.getAllGroups()
    }

    override suspend fun getGroupById(groupId: String): GroupEntity {
        return groupDao.getGroupByGroupId(groupId = groupId)
    }

    override suspend fun newGroup(name: String, path: String?, members: List<UsersEntity>, byteArray: ByteArray?): Result<Boolean> {
        if (auth.currentUser != null) {
            val groupId = "${auth.currentUser!!.uid}${System.currentTimeMillis()}"
            val groupEntity = GroupEntity(
                groupId = groupId,
                name = name,
                members = members.map { it.email }.plus(auth.currentUser!!.email!!),
                profileUrl = "",
                creatorId = auth.currentUser!!.email!!
            )
            FirebaseDatabase.getInstance()
                .getReference(Utils.GROUPS)
                .child(groupId)
                .setValue(groupEntity.toFireBaseModel()).await()
            usersRepository.addFriend(members)
            firebaseBackgroundSyncRepository.addGroupForFirebaseListener(groupId)
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
                if (user.isNotEmpty() && user != auth.currentUser!!.email) {
                    userDirectory
                        .child(Utils.formatEmailForFirebase(user))
                        .child(Utils.GROUP_INFO)
                        .child(Utils.PENDING).child(groupId).setValue(Utils.NON_ADMIN_VALUE)
                }
            }
            groupEntity.profileUrl = path ?: ""
            addGroup(groupEntity)
            return Result.success(true)
        } else {
            auth.signOut()
            return Result.failure(Exception(Utils.SESSION_EXPIRED_ERROR))
        }
    }

    override suspend fun addGroup(groupEntity: GroupEntity) {
        groupDao.addGroup(entity = groupEntity)
    }

    override suspend fun getAllExistingGroupsFromDb() = groupDao.getAllExistingGroupsFromDb()

    override suspend fun updateEntity(entity: GroupEntity) {
        groupDao.updateEntity(entity)
    }

}