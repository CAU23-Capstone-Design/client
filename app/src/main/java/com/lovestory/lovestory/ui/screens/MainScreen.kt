package com.lovestory.lovestory.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.lovestory.lovestory.graphs.MainNavGraph
import com.lovestory.lovestory.module.isServiceRunning
import com.lovestory.lovestory.services.LocationService
import com.lovestory.lovestory.services.PhotoService
//import com.lovestory.lovestory.services.getExternalStoragePermission
import com.lovestory.lovestory.services.getLocationPermission
import com.lovestory.lovestory.ui.components.BottomNaviagtionBar

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun MainScreen(navHostController: NavHostController = rememberNavController()) {
    val context = LocalContext.current
    getLocationPermission()
    LaunchedEffect(key1 = null){
        val locationIntent = Intent(context, LocationService::class.java)
//        val photoIntent = Intent(context, PhotoService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!isServiceRunning(context, LocationService::class.java)){
                context.startForegroundService(locationIntent)
            }
//            if(!isServiceRunning(context, PhotoService::class.java)){
//                context.startForegroundService(photoIntent)
//            }
        } else {
            if (!isServiceRunning(context, LocationService::class.java)){
                context.startService(locationIntent)
            }
//            if(!isServiceRunning(context, PhotoService::class.java)){
//                context.startForegroundService(photoIntent)
//            }
        }
    }

    Scaffold(
        bottomBar = {BottomNaviagtionBar(navHostController = navHostController)},
        backgroundColor = Color.White
    ) {
        MainNavGraph(navHostController = navHostController)
    }
}
