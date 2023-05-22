package com.lovestory.lovestory.module

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.ContextCompat.startForegroundService
import com.lovestory.lovestory.services.LocationService

//fun startService(context : Context){
//
//    val intent = Intent(context, LocationService::class.java)
//    if(isMyServiceRunning(LocationService::class.java)){
//        Log.d("[ACTIVITY] MainActivity", "isServiceRunning is true")
//    }else{
//        Log.d("[ACTIVITY] MainActivity", "isServiceRunning is false, start location service")
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            startForegroundService(context, intent)
//        } else {
//            startService(context)
//        }
//    }
//}