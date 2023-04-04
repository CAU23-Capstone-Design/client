package com.lovestory.lovestory.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.lovestory.lovestory.graphs.MainNavGraph
import com.lovestory.lovestory.module.isServiceRunning
import com.lovestory.lovestory.services.*
//import com.lovestory.lovestory.services.getExternalStoragePermission
import com.lovestory.lovestory.ui.components.BottomNaviagtionBar

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun MainScreen(navHostController: NavHostController = rememberNavController()) {
    val context = LocalContext.current
    getExternalStoragePermission()
    getLocationPermission()
//    getReadMediaImagePermission()


    LaunchedEffect(key1 = null){
        val locationIntent = Intent(context, LocationService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!isServiceRunning(context, LocationService::class.java)){
                context.startForegroundService(locationIntent)
            }
        } else {
            if (!isServiceRunning(context, LocationService::class.java)){
                context.startService(locationIntent)
            }
        }
    }

//    LaunchedEffect(Unit){
//        val permission = Manifest.permission.READ_EXTERNAL_STORAGE
//        val permissionResult =  ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED
//
//        if(permissionResult){
//            Log.d("MAIN-SCREEN", "권한이 없으므로 권한 승인이 필요합니다.")
////            getPermission(context)
//            getExternalStoragePermission(context)
//        }
//    }



    Scaffold(
        bottomBar = {BottomNaviagtionBar(navHostController = navHostController)},
        backgroundColor = Color.White
    ) {
        MainNavGraph(navHostController = navHostController)
    }
}


fun getPermission(context: Context){
    ActivityCompat.requestPermissions(
        context as Activity,
        arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
        100
    )
//        val externalStoragePermissionRequest =
//            rememberLauncherForActivityResult(
//            contract = ActivityResultContracts.RequestPermission()
//        ){ granted->
//            Log.d("getExternalStorage-Permission", "$granted")
//            if(granted){
//
//            }else{
//                Toast.makeText(context, "LoveStory에서 사진에 접근할 수 있도록 권한을 허용해주세요", Toast.LENGTH_SHORT).show()
//            }
//        }
}