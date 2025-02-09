package com.penny.planner.data.repositories.implementations

import android.content.Context
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.penny.planner.data.db.friends.UsersEntity
import com.penny.planner.data.repositories.interfaces.FriendsDirectoryRepository
import com.penny.planner.data.repositories.interfaces.ProfilePictureRepository
import com.penny.planner.data.workmanager.ImageDownloadWorker
import com.penny.planner.helpers.Utils
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ProfilePictureRepositoryImpl @Inject constructor(
    private val friendsDirectoryRepository: FriendsDirectoryRepository,
    @ApplicationContext private val context: Context
) : ProfilePictureRepository {

    @Inject lateinit var applicationScope: CoroutineScope
    private val map = mutableMapOf<String, UsersEntity>() // email to friend map
    val scope = CoroutineScope(Job() + Dispatchers.IO)

    override fun initialize() {
        scope.launch {
            val friends = friendsDirectoryRepository.getAllFriends()
            withContext(Dispatchers.Main) {
                for (friend in friends) {
                    map[friend.email] = friend
                }
            }
        }
    }

    private fun addUpdateFriend(friend: UsersEntity) {
        map[friend.email] = friend
    }

    override suspend fun downloadProfilePicture(entity: UsersEntity) {
        if (entity.profileImageURL.isNotEmpty()) {
            downloadImageWithWorkManager(entity)
        }
    }

    override fun findLocalImagePath(email: String): UsersEntity {
        return if (map.containsKey(email)) map[email]!!
        else UsersEntity()
    }

    private suspend fun downloadImageWithWorkManager(entity: UsersEntity) {
        // Pass data to the Worker
        val inputData = Data.Builder()
            .putString("firebaseImagePath", Utils.USER_IMAGE)
            .putString("imageId", entity.id)
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
                    entity.localImagePath = result ?: ""
                    applicationScope.launch {
                        addUpdateFriend(entity)
                        friendsDirectoryRepository.updateFriend(entity)
                    }
                }
            }
        }
    }

}