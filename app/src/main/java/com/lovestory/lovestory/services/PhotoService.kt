package com.lovestory.lovestory.services

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.database.ContentObserver
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.lovestory.lovestory.R
import com.lovestory.lovestory.broadcasts.LocationToPhoto.ACTION_START_PHOTO_PICKER_SERVICE
import com.lovestory.lovestory.broadcasts.LocationToPhoto.ACTION_STOP_PHOTO_PICKER_SERVICE

class PhotoService : Service(){
    private lateinit var contentObserver: ContentObserver
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int{
        Log.d("Photo-service", "포토 서비스 호출")

        val notification: Notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("LoveStory 사진 서비스")
            .setContentText("사진 서비스가 실행중입니다.")
            .setSmallIcon(R.drawable.lovestory_logo)
            .build()


        when (intent?.action) {
            ACTION_START_PHOTO_PICKER_SERVICE -> {
//                createNotificationChannel()

                startForeground(NOTIFICATION_ID, notification)
                Log.d("Photo-service", "포토 서비스 시작")
                // Start photo picking process
            }
            ACTION_STOP_PHOTO_PICKER_SERVICE -> {
                Log.d("Photo-service", "포토 서비스 중지")
                stopForeground(true)
                // Stop photo picking process
            }
        }
        return START_NOT_STICKY
    }

    override fun onCreate() {
        super.onCreate()
        Log.d("Photo-service", "포토 서비스 생상")
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(CHANNEL_ID, "Photo Picker Service Channel", importance)
            val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {
        private const val CHANNEL_ID = "photo_picker_service_channel"
        private const val NOTIFICATION_ID = 2
    }



//    private val broadcastReceiver = object : BroadcastReceiver() {
//        override fun onReceive(context: Context, intent: Intent) {
//            when (intent.action) {
//                "photo_picker_service_start" -> {
//                    Log.d("Photo service", "사진 픽커 서비스가 시작")
//
//                }
//                "photo_picker_service_stop" -> {
//                    Log.d("Photo service", "사진 픽커 서비스가 종료")
//                }
//            }
//        }
//    }

//    private fun registerContentObserver() {
//        contentObserver = object : ContentObserver(Handler(Looper.getMainLooper())) {
//            override fun onChange(selfChange: Boolean, uri: Uri?) {
//                super.onChange(selfChange, uri)
////                onNewPhotoAdded()
//            }
//        }
//
//        contentResolver.registerContentObserver(
//            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//            true,
//            contentObserver
//        )
//    }

//    override fun onCreate() {
//        super.onCreate()
//        // BroadcastReceiver 등록
//        LocalBroadcastManager.getInstance(this).registerReceiver(
//            broadcastReceiver,
//            IntentFilter().apply {
//                addAction("photo_picker_service_start")
//                addAction("photo_picker_service_stop")
//            }
//        )
//
//        registerContentObserver()
//    }
//
//    override fun onDestroy() {
//        // BroadcastReceiver 등록 해제
//        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver)
//        super.onDestroy()
//    }
}

@Composable
fun getExternalStoragePermission(){
    val context = LocalContext.current
    val permission = Manifest.permission.READ_EXTERNAL_STORAGE
    val permissionResult =  ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED

    val externalStoragePermissionRequest = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ){ granted->
        Log.d("getExternalStorage-Permission", "$granted")
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