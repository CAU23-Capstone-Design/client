package com.lovestory.lovestory

import android.Manifest
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
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.lovestory.lovestory.graphs.RootNavigationGraph
import com.lovestory.lovestory.services.LocationService
import com.lovestory.lovestory.ui.components.DialogForPermission
import com.lovestory.lovestory.ui.theme.LoveStoryTheme
import com.lovestory.lovestory.ui.theme.LoveStoryThemeForMD3


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)

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

            //Manifest.permission.ACCESS_MEDIA_LOCATION,
            val permissions = arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.ACCESS_MEDIA_LOCATION,
                Manifest.permission.POST_NOTIFICATIONS,
            )
            val permissionResult = ContextCompat.checkSelfPermission(applicationContext, permissions[0]) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(applicationContext, permissions[1]) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(applicationContext, permissions[2]) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(applicationContext, permissions[3]) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(applicationContext, permissions[4]) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(applicationContext, permissions[5]) == PackageManager.PERMISSION_GRANTED
            Log.d("Permission Check", "$permissionResult")

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
//                    Log.d("fdgdfgdfgdfg", "$permissionResult")
                    Log.d("Permission Check", "1 ${ContextCompat.checkSelfPermission(applicationContext, permissions[0]) == PackageManager.PERMISSION_GRANTED}")
                    Log.d("Permission Check", "2 ${ContextCompat.checkSelfPermission(applicationContext, permissions[1]) == PackageManager.PERMISSION_GRANTED}")
                    Log.d("Permission Check", "3 ${ContextCompat.checkSelfPermission(applicationContext, permissions[2]) == PackageManager.PERMISSION_GRANTED}")
                    Log.d("Permission Check", "4 ${ContextCompat.checkSelfPermission(applicationContext, permissions[3]) == PackageManager.PERMISSION_GRANTED}")
                    Log.d("Permission Check", "5 ${ContextCompat.checkSelfPermission(applicationContext, permissions[4]) == PackageManager.PERMISSION_GRANTED}")
                    Log.d("Permission Check", "6 ${ContextCompat.checkSelfPermission(applicationContext, permissions[4]) == PackageManager.PERMISSION_GRANTED}")
                    DialogForPermission(permissionResult)
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




