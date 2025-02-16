package com.penny.planner.data.repositories.implementations

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.penny.planner.data.db.friends.UsersEntity
import com.penny.planner.data.db.groups.GroupDao
import com.penny.planner.data.db.groups.GroupEntity
import com.penny.planner.data.db.monthlyexpenses.MonthlyExpenseEntity
import com.penny.planner.data.repositories.interfaces.FirebaseBackgroundSyncRepository
import com.penny.planner.data.repositories.interfaces.FriendsDirectoryRepository
import com.penny.planner.data.repositories.interfaces.GroupRepository
import com.penny.planner.data.repositories.interfaces.MonthlyExpenseRepository
import com.penny.planner.data.repositories.interfaces.ProfilePictureRepository
import com.penny.planner.data.workmanager.ImageDownloadWorker
import com.penny.planner.helpers.Utils
import com.penny.planner.models.GroupListDisplayModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

class GroupRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val groupDao: GroupDao,
    private val firebaseBackgroundSyncRepository: FirebaseBackgroundSyncRepository,
    private val monthlyExpenseRepository: MonthlyExpenseRepository,
    private val friendsDirectoryRepository: FriendsDirectoryRepository,
    private val profilePictureRepository: ProfilePictureRepository
): GroupRepository {

    val scope = CoroutineScope(Job() + Dispatchers.IO)
    private val auth = FirebaseAuth.getInstance()
    private val storage = FirebaseStorage.getInstance()
    val db = FirebaseFirestore.getInstance()

    private val userDirectory = FirebaseDatabase.getInstance().getReference(Utils.USERS)
    private val groupCollectionRef = db.collection(Utils.GROUP_EXPENSES)

    override suspend fun getAllGroupLists(): LiveData<List<GroupListDisplayModel>> {
        return groupDao.getAllGroupListForDisplay(Utils.getCurrentMonthYear())
    }

    override suspend fun getGroupById(groupId: String): GroupEntity {
        return groupDao.getGroupByGroupId(groupId = groupId)
    }

    override suspend fun newGroup(
        name: String,
        path: String?,
        monthlyBudget: Double,
        safeToSpendLimit: Int,
        byteArray: ByteArray?
    ): Result<String> {
        if (auth.currentUser != null) {
            val groupId = groupCollectionRef.document().id
            val groupEntity = GroupEntity(
                groupId = groupId,
                name = name,
                members = listOf(auth.currentUser!!.email!!),
                profileImage = "",
                creatorId = auth.currentUser!!.email!!,
                monthlyBudget = monthlyBudget,
                safeToSpendLimit = safeToSpendLimit
            )
            FirebaseDatabase.getInstance()
                .getReference(Utils.GROUPS)
                .child(groupId)
                .child(Utils.GROUP_INFO)
                .setValue(groupEntity.toFireBaseModel()).await()
            var downloadPath: Uri? = null
            if (byteArray != null) {
                val storageRef = storage.getReference(Utils.GROUP_IMAGE).child(groupId)
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
                    .child(Utils.GROUP_INFO)
                    .child(Utils.PROFILE_URL).setValue(downloadPath.toString())
            }
            userDirectory
                .child(Utils.formatEmailForFirebase(auth.currentUser!!.email!!))
                .child(Utils.GROUP_INFO)
                .child(Utils.JOINED).child(groupId).setValue(Utils.ADMIN_VALUE)
            groupEntity.profileImage = path ?: ""
            if (byteArray != null)
                groupEntity.localImagePath = saveGroupImageLocally(groupEntity, byteArray)
            addGroup(groupEntity)
            monthlyExpenseRepository.addMonthlyExpenseEntity(
                MonthlyExpenseEntity(
                    entityID = groupId,
                    month = Utils.getCurrentMonthYear(),
                    expense = 0.0
                )
            )
            firebaseBackgroundSyncRepository.addGroupForFirebaseListener(groupId)
            return Result.success(groupId)
        } else {
            auth.signOut()
            return Result.failure(Exception(Utils.SESSION_EXPIRED_ERROR))
        }
    }

    override suspend fun addGroup(groupEntity: GroupEntity) {
        groupDao.addGroup(entity = groupEntity)
    }

    override suspend fun updateGroupMembers(group: GroupEntity) {
        val members = friendsDirectoryRepository.getFriends(group.members)
        for (member in members) {
            if (Utils.moreThanADay(member.lastUpdate)) {
                val result = friendsDirectoryRepository.findUserFromServer(member.email)
                if (result.isSuccess && result.getOrNull() != null) {
                    member.lastUpdate = System.currentTimeMillis()
                    val friend = result.getOrNull()!!
                    if (friend.name != member.name) {
                        member.name = friend.name
                    }
                    if (friend.profileImageURL != member.profileImageURL) {
                        member.profileImageURL = friend.profileImageURL
                        profilePictureRepository.downloadProfilePicture(friend)
                    }
                    friendsDirectoryRepository.updateFriend(member)
                }
            }
        }
    }

    override fun isAdmin(creatorId: String) = auth.currentUser?.email == creatorId

    override suspend fun searchGroup(groupId: String): Result<GroupEntity> {
        try {
            if(!Utils.isNetworkAvailable(context))
                throw Exception(Utils.NETWORK_NOT_AVAILABLE)
            if (groupDao.doesGroupExists(groupId)) {
                throw Exception(Utils.ALREADY_A_MEMBER)
            }
            val snapshot = FirebaseDatabase.getInstance()
                .getReference(Utils.GROUPS)
                .child(groupId)
                .child(Utils.GROUP_INFO)
                .get().await()
            if (snapshot != null) {
                val entity = snapshot.getValue(GroupEntity::class.java)
                if (entity!!.status != 1)
                    throw Exception(Utils.GROUP_NOT_OPEN)
                return Result.success(entity)
            } else
                throw Exception(Utils.GROUP_NOT_FOUND)
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }

    override suspend fun joinExistingGroup(group: GroupEntity): Result<Boolean> {
        try {
            if(!Utils.isNetworkAvailable(context))
                throw Exception(Utils.NETWORK_NOT_AVAILABLE)
            FirebaseDatabase.getInstance()
                .getReference(Utils.GROUPS)
                .child(group.groupId)
                .child(Utils.APPROVALS)
                .child(auth.currentUser!!.uid)
                .setValue(UsersEntity(
                    email = auth.currentUser!!.email!!,
                    id = auth.currentUser!!.uid,
                    name = auth.currentUser!!.displayName!!
                    ).toFirebaseEntityForApprovals()
                ).await()
            FirebaseDatabase.getInstance()
                .getReference(Utils.USERS)
                .child(Utils.formatEmailForFirebase(auth.currentUser!!.email!!))
                .child(Utils.GROUP_INFO)
                .child(Utils.PENDING)
                .child(group.groupId)
                .setValue(0).await()
            group.isPending = true
            groupDao.addGroup(group)
            monthlyExpenseRepository.addMonthlyExpenseEntity(
                MonthlyExpenseEntity(
                    entityID = group.groupId,
                    month = Utils.getCurrentMonthYear(),
                    expense = 0.0
                )
            )
            downloadGroupImage(group)
            return Result.success(true)
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }

    private fun saveGroupImageLocally(group: GroupEntity, byteArray: ByteArray): String {
        val localFile = File(context.filesDir, "${group.groupId}.jpeg")
        try {
            val fos = FileOutputStream(localFile.path)
            fos.write(byteArray)
            fos.close()
        } catch (e: java.lang.Exception) {
            return ""
        }
        return localFile.absolutePath
    }

    override suspend fun getApprovalList(groupId: String): Result<List<UsersEntity>> {
        try {
            if(!Utils.isNetworkAvailable(context))
                throw Exception(Utils.NETWORK_NOT_AVAILABLE)
            val snapshot = FirebaseDatabase.getInstance()
                .getReference(Utils.GROUPS)
                .child(groupId)
                .child(Utils.APPROVALS)
                .get().await()
            if (snapshot.exists()) {
                val list = mutableListOf<UsersEntity>()
                for (child in snapshot.children) {
                    var approvalItem = child.getValue(UsersEntity::class.java)
                    if (approvalItem != null) {
                        if (profilePictureRepository.findLocalImagePath(approvalItem.email).name.isEmpty()) {
                            friendsDirectoryRepository.addFriend(approvalItem)
                            profilePictureRepository.downloadProfilePicture(approvalItem)
                        } else {
                            approvalItem = profilePictureRepository.findLocalImagePath(approvalItem.email)
                        }
                        list.add(approvalItem)
                    }
                }
                if (list.isNotEmpty()) {
                    groupDao.updateEntityPendingMemberStatus(groupId, true)
                }
                return Result.success(list)
            }
            throw Exception(Utils.NO_PENDING_REQUESTS)
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }

    override suspend fun approveToJoin(group: GroupEntity, usersEntity: UsersEntity, needUpdatePendingFlag: Boolean): Result<Boolean> {
        try {
            if(!Utils.isNetworkAvailable(context))
                throw Exception(Utils.NETWORK_NOT_AVAILABLE)

            userDirectory
                .child(Utils.formatEmailForFirebase(usersEntity.email))
                .child(Utils.GROUP_INFO)
                .child(Utils.PENDING)
                .child(group.groupId)
                .setValue(1).await()

            FirebaseDatabase.getInstance()
                .getReference(Utils.GROUPS)
                .child(group.groupId)
                .child(Utils.GROUP_INFO)
                .child(Utils.MEMBERS)
                .child(group.members.size.toString())
                .setValue(usersEntity.email)
                .await()

            FirebaseDatabase.getInstance()
                .getReference(Utils.GROUPS)
                .child(group.groupId)
                .child(Utils.APPROVALS)
                .child(usersEntity.id)
                .removeValue().await()

            if (needUpdatePendingFlag)
                groupDao.updateEntityPendingMemberStatus(group.groupId, false)
            return Result.success(true)
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }

    override suspend fun rejectRequestToJoin(group: GroupEntity, usersEntity: UsersEntity, needUpdatePendingFlag: Boolean): Result<Boolean> {
        try {
            if(!Utils.isNetworkAvailable(context))
                throw Exception(Utils.NETWORK_NOT_AVAILABLE)

            userDirectory
                .child(Utils.formatEmailForFirebase(usersEntity.email))
                .child(Utils.GROUP_INFO)
                .child(Utils.PENDING)
                .child(group.groupId)
                .setValue(2).await()

            FirebaseDatabase.getInstance()
                .getReference(Utils.GROUPS)
                .child(group.groupId)
                .child(Utils.APPROVALS)
                .child(usersEntity.id)
                .removeValue().await()

            if (needUpdatePendingFlag)
                groupDao.updateEntityPendingMemberStatus(group.groupId, false)
            return Result.success(true)
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }

    override suspend fun rejectAll(groupId: String): Result<Boolean> {
        try {
            if(!Utils.isNetworkAvailable(context))
                throw Exception(Utils.NETWORK_NOT_AVAILABLE)

            FirebaseDatabase.getInstance()
                .getReference(Utils.GROUPS)
                .child(groupId)
                .child(Utils.GROUP_INFO)
                .child(Utils.STATUS)
                .setValue(0).await()

            FirebaseDatabase.getInstance()
                .getReference(Utils.GROUPS)
                .child(groupId)
                .child(Utils.APPROVALS)
                .removeValue().await()

            groupDao.updateEntityPendingMemberStatus(groupId, false)

            return Result.success(true)
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }

    override suspend fun checkUpdateOfPendingGroups() {
        val pendingGroups = groupDao.getAllPendingGroups()
        for (groupId in pendingGroups) {
            deleteFromPendingIfClosed(groupId)
        }
    }

    private suspend fun deleteFromPendingIfClosed(groupId: String){
        try {
            if(!Utils.isNetworkAvailable(context))
                throw Exception(Utils.NETWORK_NOT_AVAILABLE)
            val snapshot = FirebaseDatabase.getInstance()
                .getReference(Utils.GROUPS)
                .child(groupId)
                .child(Utils.GROUP_INFO)
                .get().await()
            if (snapshot != null) {
                val entity = snapshot.getValue(GroupEntity::class.java)
                if (entity!!.status != 1) {
                    FirebaseDatabase.getInstance()
                        .getReference(Utils.USERS)
                        .child(Utils.formatEmailForFirebase(auth.currentUser!!.email!!))
                        .child(Utils.GROUP_INFO)
                        .child(Utils.PENDING)
                        .child(groupId)
                        .setValue(2).await()
                    groupDao.delete(groupId)
                }
            }

        } catch (e: Exception) {
            return
        }
    }

    private suspend fun downloadGroupImage(group: GroupEntity) {
        Log.d("downloadGroupImage", group.groupId)
        Log.d("downloadGroupImage", "group not present")
        val inputData = Data.Builder()
            .putString("firebaseImagePath", Utils.GROUP_IMAGE)
            .putString("imageId", group.groupId)
            .build()

        // Create a WorkRequest
        val workRequest = OneTimeWorkRequestBuilder<ImageDownloadWorker>()
            .setInputData(inputData)
            .build()

        // Enqueue the WorkRequest
        val workManager = WorkManager.getInstance(context)
        workManager.enqueue(workRequest)
        withContext(Dispatchers.Main) {
            workManager.getWorkInfoByIdLiveData(workRequest.id).observeForever { workInfo ->
                if (workInfo != null && workInfo.state.isFinished) {
                    val result = workInfo.outputData.getString("resultKey")
                    group.localImagePath = result ?: ""
                    scope.launch {
                        groupDao.updatePicturePath(group.groupId, group.localImagePath)
                    }
                }
            }
        }
    }
}