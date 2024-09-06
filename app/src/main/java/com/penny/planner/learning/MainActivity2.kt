package com.penny.planner.learning

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.app.ActivityCompat
import com.penny.planner.learning.service.TestForegroundService
import com.penny.planner.ui.theme.PennyPlannerTheme
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp

@AndroidEntryPoint
class MainActivity2 : ComponentActivity() {

    private val serviceIntent by lazy {
        Intent(this, TestForegroundService::class.java)
    }

    private var service : TestForegroundService? = null

    private val bindServiceConnection by lazy {
        object : ServiceConnection {
            override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
                val binder = p1 as TestForegroundService.TestBinder
                service = binder.getService()
            }

            override fun onServiceDisconnected(p0: ComponentName?) {
                service?.stopSelf()
                service = null
            }

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                    0
                )
            }
        }

        setContent {
            PennyPlannerTheme {
                HomeScreen()
            }
        }
    }

}