package com.lovestory.lovestory.services

import android.Manifest
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.IBinder
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

import android.app.Notification
import androidx.core.app.NotificationCompat
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.google.android.gms.location.*
import com.lovestory.lovestory.R
import com.lovestory.lovestory.module.getToken
import com.lovestory.lovestory.module.saveLocation
import com.lovestory.lovestory.network.sendLocationToServer

class LocationService : Service() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val context = this
        Log.d("Locate - Service", "포그라운드 서비스 시작")
        createNotificationChannel()

        val notification: Notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("앱이 실행 중입니다")
            .setContentText("앱이 백그라운드에서 실행 중입니다.")
            .setSmallIcon(R.drawable.lovestory_logo)
            .build()

        startForeground(NOTIFICATION_ID, notification)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val permission = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
        val permissionResult = ContextCompat.checkSelfPermission(this, permission[0]) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, permission[1]) != PackageManager.PERMISSION_GRANTED

        if (!permissionResult) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let {
                    // Do something with the location
                    Log.d("Location", "${location.longitude} ${location.latitude}")
                }
            }

            val locationRequest = com.google.android.gms.location.LocationRequest
                .Builder(Priority.PRIORITY_HIGH_ACCURACY, 10 * 1000)
                .build()

            val locationCallbackForMyApp = object : LocationCallback(){
                override fun onLocationResult(locationResult: LocationResult) {
                    // 새로 요청된 위치 정보
                    for (location in locationResult.locations){
                        location.latitude
                        location.longitude
                        val token = getToken(context)
                        saveLocation(token, location.latitude, location.longitude)
                        Log.d("LOCATION-SERVICE", "current location : latitude ${location.latitude}, longitude : ${location.longitude}")
                    }
                }
            }
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallbackForMyApp,
                Looper.getMainLooper()
            )
        }
        return START_STICKY
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(CHANNEL_ID, "Location Service Channel", importance)
            val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {
        private const val CHANNEL_ID = "location_service_channel"
        private const val NOTIFICATION_ID = 1
    }
}

@Composable
fun getLocationPermission(){
    val context = LocalContext.current
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
                Toast.makeText(context, "정확한 위치 확인을 위해서 정확한 위치 권한으로 설정해주세요", Toast.LENGTH_SHORT).show()
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
    }
}


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
                Toast.makeText(context, "정확한 위치 확인을 위해서 정확한 위치 권한으로 설정해주세요", Toast.LENGTH_SHORT).show()
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
    }
}



//        val locationRequest = LocationRequest.Builder(5000).build()

//        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallbackForMyApp)

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



//            val locationRequest = LocationRequest.create().apply {
//                priority = Priority.PRIORITY_HIGH_ACCURACY
//                interval = 60_000 // 1 minute
//                fastestInterval = 30_000 // 30 seconds
//            }
//
//            val locationCallbackForMyApp = object : LocationCallback() {
//                override fun onLocationResult(locationResult: LocationResult) {
//                    // 새로 요청된 위치 정보
//                    for (location in locationResult.locations) {
//                        Log.d("LOCATION-SERVICE", "current location : latitude ${location.latitude}, longitude : ${location.longitude}")
//                    }
//                }
//            }
//
//            fusedLocationClient.requestLocationUpdates(
//                locationRequest,
//                locationCallbackForMyApp,
//                Looper.getMainLooper()
//            )









