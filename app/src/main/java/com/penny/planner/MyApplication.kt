package com.penny.planner

import android.app.Application
import com.penny.planner.data.repositories.interfaces.CategoryAndEmojiRepository
import com.penny.planner.data.repositories.interfaces.FirebaseBackgroundSyncRepository
import com.penny.planner.data.repositories.interfaces.ProfilePictureRepository
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import javax.inject.Inject

@HiltAndroidApp
class MyApplication : Application() {

    @Inject lateinit var categoryAndEmojiRepository: CategoryAndEmojiRepository
    @Inject lateinit var firebaseBackgroundRepository: FirebaseBackgroundSyncRepository
    @Inject lateinit var profilePictureRepository: ProfilePictureRepository

    private val applicationScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override fun onCreate() {
        super.onCreate()
        categoryAndEmojiRepository.checkServerAndUpdateCategory()
        firebaseBackgroundRepository.init()
        profilePictureRepository.initialize()
//        createChannelIds()
    }

    override fun onTerminate() {
        super.onTerminate()
        applicationScope.cancel()
    }

// Note : Creating an existing notification channel with its original values performs no operation,
// so it's safe to call this code when starting an app.

//    private fun createChannelIds() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val name = "TestChannel"
//            val descriptionTxt = "Testing"
//            val importance = NotificationManager.IMPORTANCE_HIGH
//            val notification = NotificationChannel("Penny", name, importance).apply {
//                description = descriptionTxt
//            }
//            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//            notificationManager.createNotificationChannel(notification)
//        }
//    }
}

