package com.lovestory.lovestory.services

import android.Manifest
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.core.content.ContextCompat
import android.app.Notification
import androidx.core.app.NotificationCompat
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.*
import com.google.android.gms.location.*
import com.lovestory.lovestory.R
import com.lovestory.lovestory.broadcasts.LocationToPhoto.ACTION_START_PHOTO_PICKER_SERVICE
import com.lovestory.lovestory.broadcasts.LocationToPhoto.ACTION_STOP_PHOTO_PICKER_SERVICE
import com.lovestory.lovestory.module.getToken
import com.lovestory.lovestory.module.saveLocation
import com.lovestory.lovestory.network.getNearbyCoupleFromServer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LocationService : Service() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var backgroundHandler: Handler
    private lateinit var handlerThread: HandlerThread

    private var isPhotoServiceRunning = false

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        handlerThread.quitSafely()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val context = this
        Log.d("Locate - Service", "포그라운드 서비스 시작")
        createNotificationChannel()

        val notification: Notification = createNotificationForLocationService()

        startForeground(NOTIFICATION_ID, notification)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val permission = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
        val permissionResult = ContextCompat.checkSelfPermission(this, permission[0]) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, permission[1]) != PackageManager.PERMISSION_GRANTED

        if (!permissionResult) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let {

                    Log.d("Location", "${location.longitude} ${location.latitude}")
                }
            }

            val locationRequest = com.google.android.gms.location.LocationRequest
                .Builder(Priority.PRIORITY_HIGH_ACCURACY, 10 * 1000)
                .build()

            val locationCallbackForMyApp = object : LocationCallback(){
                override fun onLocationResult(locationResult: LocationResult) {

                    for (location in locationResult.locations){
                        location.latitude
                        location.longitude
                        val token = getToken(context)
                        saveLocation(token, location.latitude, location.longitude)
                        CoroutineScope(Dispatchers.IO).launch{
                            val response = getNearbyCoupleFromServer(token)
                            if(response.isSuccessful){
//                                Log.d("check nearby Location", "${response.body()}")
                                if(response.body()!!.isNearby){
                                    sendBroadcastToSecondService(ACTION_START_PHOTO_PICKER_SERVICE)
                                }
                                else{
                                    sendBroadcastToSecondService(ACTION_STOP_PHOTO_PICKER_SERVICE)
                                }

                            }else{
                                Log.e("check nearby location error" , "${response.errorBody()}")
                                sendBroadcastToSecondService(ACTION_STOP_PHOTO_PICKER_SERVICE)
                            }
                        }
//                        Log.d("LOCATION-SERVICE", "current location : latitude ${location.latitude}, longitude : ${location.longitude}")
                    }
                }
            }
            handlerThread = HandlerThread("LocationServiceBackground")
            handlerThread.start()
            backgroundHandler = Handler(handlerThread.looper)

            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallbackForMyApp,
                backgroundHandler.looper
            )
        }else{
//            getLocationPermission()
        }
        return START_STICKY
    }

    private fun sendBroadcastToSecondService(action: String) {
        val intent = Intent(this, PhotoService::class.java)
        intent.action = action
        when (intent.action) {
            ACTION_START_PHOTO_PICKER_SERVICE -> {
                if (!isPhotoServiceRunning) {
                    isPhotoServiceRunning = true
                    startForegroundService(intent)
                }
            }
            ACTION_STOP_PHOTO_PICKER_SERVICE -> {
                if (isPhotoServiceRunning) {
                    isPhotoServiceRunning = false
                    stopService(intent)
                }
            }
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

    private fun createNotificationForLocationService():Notification{
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("LoveStory 사진 서비스")
            .setContentText("사진 서비스가 실행중입니다.")
            .setSmallIcon(R.drawable.lovestory_logo)
            .build()
    }

    companion object {
        private const val CHANNEL_ID = "location_service_channel"
        private const val NOTIFICATION_ID = 1
    }
}







