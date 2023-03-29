package com.lovestory.lovestory.ui.screens

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat.startForegroundService
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.lovestory.lovestory.graphs.MainNavGraph
import com.lovestory.lovestory.module.isServiceRunning
import com.lovestory.lovestory.services.GetDeviceLocation
import com.lovestory.lovestory.services.LocationService
import com.lovestory.lovestory.services.getLocationPermission
import com.lovestory.lovestory.ui.components.BottomNaviagtionBar

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun MainScreen(navHostController: NavHostController = rememberNavController()) {
    val context = LocalContext.current
    getLocationPermission()
    LaunchedEffect(key1 = null){
        val intent = Intent(context, LocationService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!isServiceRunning(context, LocationService::class.java)){
                context.startForegroundService(intent)
            }
        } else {
            if (!isServiceRunning(context, LocationService::class.java)){
                context.startService(intent)
            }
        }
    }

    Scaffold(
        bottomBar = {BottomNaviagtionBar(navHostController = navHostController)},
        backgroundColor = Color.White
    ) {
//        GetDeviceLocation { location ->
//            Log.d("Main app screen", "Location : $location")
//        }
        MainNavGraph(navHostController = navHostController)
    }
}

