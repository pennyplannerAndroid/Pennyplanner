package com.penny.planner

import android.app.Application
import com.penny.planner.data.repositories.interfaces.CategoryAndEmojiRepository
import com.penny.planner.data.repositories.interfaces.GroupRepository
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class MyApplication : Application() {

    @Inject lateinit var categoryAndEmojiRepository: CategoryAndEmojiRepository
    @Inject lateinit var groupRepository: GroupRepository

    override fun onCreate() {
        super.onCreate()
        categoryAndEmojiRepository.checkServerAndUpdateCategory()
        groupRepository.getAllPendingGroups()
//        createChannelIds()
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

