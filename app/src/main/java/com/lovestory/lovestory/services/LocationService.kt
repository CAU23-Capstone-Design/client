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
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.location.*
import com.lovestory.lovestory.R
import com.lovestory.lovestory.broadcasts.LocationToPhoto.ACTION_START_PHOTO_PICKER_SERVICE
import com.lovestory.lovestory.broadcasts.LocationToPhoto.ACTION_STOP_PHOTO_PICKER_SERVICE
import com.lovestory.lovestory.module.checkNearby
import com.lovestory.lovestory.module.getToken
import com.lovestory.lovestory.module.saveLocation
import com.lovestory.lovestory.network.getNearbyCoupleFromServer
import com.lovestory.lovestory.network.sendLocationToServer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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
            .setContentTitle("LoveStory 위치 서비스")
            .setContentText("위치 서비스가 실행중입니다.")
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
                        CoroutineScope(Dispatchers.Main).launch{
                            val response = getNearbyCoupleFromServer(token)
                            if(response.isSuccessful){
                                Log.d("check nearby Location", "${response.body()}")
                                if(response.body()!!.isNearby){
                                    Log.d("LOCATION-SERVICE", "포토 서비스 시작 호출")

                                    sendBroadcastToSecondService(ACTION_START_PHOTO_PICKER_SERVICE)
                                }
                                else{
                                    Log.d("LOCATION-SERVICE", "포토 서비스 종료 호출 ")
                                    sendBroadcastToSecondService(ACTION_STOP_PHOTO_PICKER_SERVICE)
                                }

                            }else{
                                Log.e("check nearby location error" , "${response.errorBody()}")
                                Log.d("LOCATION-SERVICE", "포토 서비스 종료 호출 ")
                                sendBroadcastToSecondService(ACTION_STOP_PHOTO_PICKER_SERVICE)
                            }
                        }
//                        val result  = checkNearby(token)
                        Log.d("LOCATION-SERVICE", "current location : latitude ${location.latitude}, longitude : ${location.longitude}")
//                        if(result){
//
//                        }else{
//
//                        }
                    }
                }
            }
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallbackForMyApp,
                Looper.getMainLooper()
            )
        }else{
//            getLocationPermission()
        }
        return START_STICKY
    }

    private fun sendBroadcastToSecondService(action: String) {
//        val intent = Intent(action)
//        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
        val intent = Intent(this, PhotoService::class.java)
        intent.action = action
        if (action == ACTION_START_PHOTO_PICKER_SERVICE) {
            startForegroundService(intent)
        } else {
            stopService(intent)
        }
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






