package com.lovestory.lovestory.services

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

@Composable
fun GetDeviceLocation(onLocationReceived: (Location) -> Unit) {
    val context = LocalContext.current
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let {
                    onLocationReceived(it)
                }
            }
        }else{
            Toast.makeText(context, "상대방과 위치 확인을 위해서 위치 권한을 켜주세요", Toast.LENGTH_SHORT).show()
        }
    }

    val permission = Manifest.permission.ACCESS_FINE_LOCATION
    val permissionResult = ContextCompat.checkSelfPermission(context, permission)

    if (permissionResult != PackageManager.PERMISSION_GRANTED) {
        SideEffect {
            requestPermissionLauncher.launch(permission)
        }

    } else {
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let {
                onLocationReceived(it)
            }
        }
    }
}