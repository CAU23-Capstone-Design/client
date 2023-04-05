package com.lovestory.lovestory.module

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

@Composable
fun getLocationPermission(){
    val context = LocalContext.current
    val permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
    val permissionResult = ContextCompat.checkSelfPermission(context, permissions[0]) != PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(context, permissions[1]) != PackageManager.PERMISSION_GRANTED

    val locationPermissionRequest = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ){permissions ->
        when{
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                Toast.makeText(context, "정확한 위치 권한 승인", Toast.LENGTH_SHORT).show()
            }
            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                Toast.makeText(context, "대략적인 위치 권한 승인", Toast.LENGTH_SHORT).show()
            }
            else -> {
                Toast.makeText(context, "위치 권한 얻기 실패...", Toast.LENGTH_SHORT).show()
            }
        }

    }
    if (permissionResult) {
        SideEffect {
            locationPermissionRequest.launch(permissions)
        }
    }
}


@Composable
fun getMediaPermission() {
    Log.d("FUNCTION-requestMediaPermission", "권한체크")
    val context = LocalContext.current
    val permission = Manifest.permission.READ_MEDIA_IMAGES
    val permissionResult =  ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED

    val externalStoragePermissionRequest = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ){ granted->
        Log.d("FUNCTION-requestMediaPermission", "$granted")
        if(granted){

        }else{
            Toast.makeText(context, "LoveStory에서 사진에 접근할 수 있도록 권한을 허용해주세요", Toast.LENGTH_SHORT).show()
        }
    }

    if(permissionResult){
        SideEffect {
            externalStoragePermissionRequest.launch(permission)
        }
    }
}