package com.lovestory.lovestory

import android.Manifest
import android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.util.Log
import android.view.Window
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.lovestory.lovestory.graphs.RootNavigationGraph
import com.lovestory.lovestory.services.LocationService
import com.lovestory.lovestory.ui.theme.LoveStoryTheme
import com.lovestory.lovestory.ui.theme.LoveStoryThemeForMD3


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)

        val requestPermissionLauncher =
            registerForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions()
            ) {permissions->
                if(permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false)) {
//                Toast.makeText(this, "정확한 위치 권한 승인", Toast.LENGTH_SHORT).show()

            }
                else if(permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false)){
//                Toast.makeText(this, "대략적인 위치 권한 승인", Toast.LENGTH_SHORT).show()
            }
                else if(permissions.getOrDefault(Manifest.permission.READ_MEDIA_IMAGES, false)){
//                    Toast.makeText(this, " 사진 권한 승인", Toast.LENGTH_SHORT).show()
                }
                else if(permissions.getOrDefault(Manifest.permission.POST_NOTIFICATIONS, false)){
//                    Toast.makeText(this, " 알람 권한 승인", Toast.LENGTH_SHORT).show()
                }
                else{
//                    Toast.makeText(this, "권한 얻기 실패...", Toast.LENGTH_SHORT).show()
//                    ActivityResultContracts.RequestMultiplePermissions()
                }
            }

        val permissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.POST_NOTIFICATIONS,
            Manifest.permission.ACCESS_MEDIA_LOCATION,
        )
        requestPermissionLauncher.launch(permissions)

        setContent {
            val systemUiController = rememberSystemUiController()
            val useDarkIcons = MaterialTheme.colors.isLight

            val lm = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            if(!lm.isLocationEnabled){
//                intent.action = Settings.
            }

            val pm = getSystemService(Context.POWER_SERVICE) as PowerManager
            if (!pm.isIgnoringBatteryOptimizations(packageName)){
                val intent = Intent()
                intent.action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
                intent.data = Uri.parse("package:$packageName")
                startActivity(intent)
            }

            Log.d("[ACTIVITY] MainActivity", "$applicationContext")
            Log.d("[ACTIVITY] MainActivity", "$applicationContext")
            val intent = Intent(this, LocationService::class.java)
            if(isMyServiceRunning(LocationService::class.java)){
                Log.d("[ACTIVITY] MainActivity", "isServiceRunning is true")
            }else{
                Log.d("[ACTIVITY] MainActivity", "isServiceRunning is false, start location service")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(intent)
                } else {
                    startService(intent)
                }
            }


            SideEffect {
                systemUiController.setSystemBarsColor(
                    color = Color(0xFFF3F3F3),
                    darkIcons = useDarkIcons
                )
            }

            LoveStoryThemeForMD3() {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = androidx.compose.material3.MaterialTheme.colorScheme.background
                ) {
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

@Composable
fun LoveStoryMainScreen() {
//    val navController = rememberNavController()
    Scaffold(
        bottomBar = { }
    ) {
        Box(Modifier.padding(it)){

        }
    }
}


@Preview(showSystemUi = true)
@Composable
fun DefaultPreview() {
    LoveStoryTheme {
        RootNavigationGraph()
    }
}




