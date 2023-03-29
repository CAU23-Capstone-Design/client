package com.lovestory.lovestory.services

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationRequest
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
//import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

@Composable
fun GetDeviceLocation(onLocationReceived: (Location) -> Unit) {
    val context = LocalContext.current
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    val permission = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
    val permissionResult = ContextCompat.checkSelfPermission(context, permission[0]) != PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(context, permission[1]) != PackageManager.PERMISSION_GRANTED

    val locationPermissionRequest = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ){permissions ->
        when{
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {

            }
            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {

            }
            else -> {
                Toast.makeText(context, "상대방과 위치 확인을 위해서 위치 권한을 켜주세요", Toast.LENGTH_SHORT).show()
            }
        }
    }

    if (permissionResult) {
        SideEffect {
            locationPermissionRequest.launch(permission)
        }

    } else {
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let {
                onLocationReceived(it)
            }
        }
        val locationRequest = com.google.android.gms.location.LocationRequest
            .Builder(Priority.PRIORITY_HIGH_ACCURACY, 10 * 1000)
            .build()

        val locationCallbackForMyApp = object : LocationCallback(){
            override fun onLocationResult(locationResult: LocationResult) {
//        super.onLocationResult(p0)
                // 새로 요청된 위치 정보
                for (location in locationResult.locations){
                    location.latitude
                    location.longitude
                    Log.d("LOCATION-SERVICE", "current location : latitude ${location.latitude}, longitude : ${location.longitude}")
                }
            }
        }

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallbackForMyApp,
            Looper.getMainLooper()
        )
//        val locationRequest = LocationRequest.Builder(5000).build()

//        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallbackForMyApp)

    }

}



//    val requestPermissionLauncher = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.RequestPermission()
//    ) { isGranted ->
//        if (isGranted) {
//            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
//                location?.let {
//                    onLocationReceived(it)
//                }
//            }
//        }else{
//            Toast.makeText(context, "상대방과 위치 확인을 위해서 위치 권한을 켜주세요", Toast.LENGTH_SHORT).show()
//        }
//    }









