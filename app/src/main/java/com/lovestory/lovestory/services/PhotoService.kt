package com.lovestory.lovestory.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.lovestory.lovestory.R

class PhotoService : Service(){
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int{
        val context = this
        Log.d("Gallery - Service", "포그라운드 서비스 시작")
        createNotificationChannel()

        val notification: Notification = NotificationCompat.Builder(this,
            CHANNEL_ID
        )
            .setContentTitle("LoveStory 동기화")
            .setContentText("사진 서비스가 실행중입니다.")
            .setSmallIcon(R.drawable.lovestory_logo)
            .build()

        startForeground(NOTIFICATION_ID, notification)

        // gallery service code


        return START_STICKY
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(CHANNEL_ID, "Photo Service Channel", importance)
            val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {
        private const val CHANNEL_ID = "photo_service_channel"
        private const val NOTIFICATION_ID = 1
    }
}