package com.penny.planner

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
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

