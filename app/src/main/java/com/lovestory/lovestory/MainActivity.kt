package com.lovestory.lovestory

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.util.Log
import android.view.Window
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.lovestory.lovestory.database.PhotoDatabase
import com.lovestory.lovestory.graphs.RootNavigationGraph
import com.lovestory.lovestory.module.StartService
import com.lovestory.lovestory.services.LocationService
import com.lovestory.lovestory.ui.components.DialogForPermission
import com.lovestory.lovestory.ui.theme.LoveStoryTheme


class MainActivity : ComponentActivity() {
    lateinit var database: PhotoDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        database = PhotoDatabase.getDatabase(this)

        setContent {
            val systemUiController = rememberSystemUiController()
            val useDarkIcons = MaterialTheme.colors.isLight

            val pm = getSystemService(Context.POWER_SERVICE) as PowerManager
            if (!pm.isIgnoringBatteryOptimizations(packageName)){
                val intent = Intent()
                intent.action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
                intent.data = Uri.parse("package:$packageName")
                startActivity(intent)
            }

            val resultIsRunningService = isMyServiceRunning(LocationService::class.java)

            SideEffect {
                systemUiController.setSystemBarsColor(
                    color = Color(0xFFF3F3F3),
                    darkIcons = useDarkIcons
                )
            }

            LoveStoryTheme() {
                Log.d("[MainActivity]", "Love Story Build ver [ad3a2f7]")
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = androidx.compose.material3.MaterialTheme.colorScheme.background
                ) {
                    if(!resultIsRunningService){
                        StartService(LocalContext.current)
                    }
                    DialogForPermission()
                    RootNavigationGraph()
                }
            }
        }
    }

    private fun isMyServiceRunning(serviceClass: Class<*>): Boolean {
        val manager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }
}
