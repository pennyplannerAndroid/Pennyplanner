package com.penny.planner.learning.service

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import com.penny.planner.ui.activities.MainActivity
import com.penny.planner.R
import java.lang.Thread.sleep
import java.util.concurrent.Executors

class TestForegroundService: Service() {

    private val binder by lazy {
        TestBinder()
    }

    enum class Action {
        START, END
    }

    val executor = Executors.newSingleThreadExecutor()
    override fun onBind(p0: Intent?): IBinder {
        return binder
    }

    override fun onCreate() {
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            Action.START.toString() -> start()
            Action.END.toString() -> stopSelf()
        }
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
    }
    // Note : The notification will take roughly 10 second to show up starting android 12
    // Exception for the following cases where it shows up immediately  :
    //1. The service is associated with a notification that includes action buttons.
    //2. The service has a foregroundServiceType of mediaPlayback, mediaProjection, or phoneCall.
    //3. The service provides a use case related to phone calls, navigation, or media playback, as defined in the notification's category attribute.
    //4. The service has opted out of the behavior change by passing FOREGROUND_SERVICE_IMMEDIATE into setForegroundServiceBehavior() when setting up the notification.

    fun start() {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        val notification = NotificationCompat.Builder(this, "Penny")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Testing in penny")
            .setContentText("This is the body")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setOngoing(true)
            .addAction(R.drawable.error_image, "Click", pendingIntent)
            .build()
        executor.execute {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                ServiceCompat.startForeground(this, 1, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_CAMERA)
            } else
                startForeground(1, notification)
            sleep(10000)
            stopSelf()
        }
    }

    inner class TestBinder() : Binder() {
        fun getService() : TestForegroundService = this@TestForegroundService
    }

}