package com.lovestory.lovestory.module

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.core.content.ContextCompat
import com.lovestory.lovestory.ui.components.AskBackgroundLocationDialog

val permissions = arrayOf(
    Manifest.permission.ACCESS_FINE_LOCATION,
    Manifest.permission.ACCESS_COARSE_LOCATION,
    Manifest.permission.ACCESS_BACKGROUND_LOCATION,
    Manifest.permission.READ_MEDIA_IMAGES,
    Manifest.permission.ACCESS_MEDIA_LOCATION,
    Manifest.permission.POST_NOTIFICATIONS,
)

@Composable
fun getResultPermissionCheck(context: Context): Boolean {
    return (ContextCompat.checkSelfPermission(
        context,
        permissions[0]
    ) == PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(
        context,
        permissions[1]
    ) == PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(
        context,
        permissions[2]
    ) == PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(
        context,
        permissions[3]
    ) == PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(
        context,
        permissions[4]
    ) == PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(
        context,
        permissions[5]
    ) == PackageManager.PERMISSION_GRANTED)
}

@Composable
fun getPermissionLauncher(context : Context): ManagedActivityResultLauncher<String, Boolean> {
    val isAskBackgroundPermissionDialog = remember { mutableStateOf(false) }
    val onDismissRequestForAskBackgroundPermission : ()->Unit = {isAskBackgroundPermissionDialog.value = false}

    val requestBackgroundLocationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = {
            if(it){
            }else{
                Toast.makeText(context, " 백그라운드 권한 거부", Toast.LENGTH_SHORT).show()
            }
        }
    )

    val requestLocationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = {permissions->
            if(permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false)) {
                val isGrantedBackgroundPermission = ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
                Log.d("COMPOSABLE-getPermissionLauncher", "$isGrantedBackgroundPermission")
                if(!isGrantedBackgroundPermission){
                    isAskBackgroundPermissionDialog.value = true
                }
//                requestBackgroundLocationPermissionLauncher.launch(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
            }
            else if(permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false)){
                Toast.makeText(context, "대략적인 위치 권한 승인", Toast.LENGTH_SHORT).show()
            }else{

            }
        }
    )

    val requestNotificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = {
            if(it){
                requestLocationPermissionLauncher.launch(arrayOf<String>(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ))
            }else{
                Toast.makeText(context, " 알람 권한 거부", Toast.LENGTH_SHORT).show()
            }
        }
    )

    val requestPhotoLocationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = {
            if(it){
                requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }else{
                Toast.makeText(context, " 사진 위치 권한 거부", Toast.LENGTH_SHORT).show()
            }
        }
    )

    val requestPhotoPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = {
            if(it){
                requestPhotoLocationPermissionLauncher.launch(Manifest.permission.ACCESS_MEDIA_LOCATION)
            }else{
                Toast.makeText(context, " 사진 권한 거부", Toast.LENGTH_SHORT).show()
            }
        }
    )

    AnimatedVisibility(isAskBackgroundPermissionDialog.value, enter= fadeIn(), exit= fadeOut()){
        AskBackgroundLocationDialog(
            onDismissRequest = onDismissRequestForAskBackgroundPermission,
            isDialogOpen = isAskBackgroundPermissionDialog,
            requestBackgroundLocationPermissionLauncher = requestBackgroundLocationPermissionLauncher
        )
    }

    return requestPhotoPermissionLauncher
}