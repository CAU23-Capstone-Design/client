package com.lovestory.lovestory.services

import android.Manifest
import android.annotation.SuppressLint
import android.app.*
import android.content.*
import android.content.pm.PackageManager
import android.database.ContentObserver
import android.database.Cursor
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.lovestory.lovestory.MainActivity
import com.lovestory.lovestory.R
import com.lovestory.lovestory.broadcasts.LocationToPhoto.ACTION_START_PHOTO_PICKER_SERVICE
import com.lovestory.lovestory.broadcasts.LocationToPhoto.ACTION_STOP_PHOTO_PICKER_SERVICE
import com.lovestory.lovestory.database.PhotoDatabase
import com.lovestory.lovestory.database.entities.PhotoForSync
import com.lovestory.lovestory.module.getInfoFromImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.math.BigInteger
import java.security.MessageDigest

class PhotoService : Service(){
    private lateinit var contentObserver: ContentObserver

    private lateinit var handlerThread: HandlerThread
    private lateinit var backgroundHandler: Handler

    private lateinit var photoDatabase: PhotoDatabase

    private val ioScope = CoroutineScope(Dispatchers.IO)

    override fun onBind(intent: Intent?): IBinder? {return null}

    override fun onCreate() {
        super.onCreate()
        Log.d("Photo-service", "포토 서비스 생성")
        photoDatabase = PhotoDatabase.getDatabase(applicationContext)
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int{
        Log.d("Photo-service", "포토 서비스 호출")

        val notification: Notification = createNotificationForPhotoService()

        when (intent?.action) {
            ACTION_START_PHOTO_PICKER_SERVICE -> {
                createNotificationChannel()
                startForeground(NOTIFICATION_ID, notification)
                Log.d("Photo-service", "포토 서비스 시작")
                registerContentObserver()
            }
            ACTION_STOP_PHOTO_PICKER_SERVICE -> {
                Log.d("Photo-service", "포토 서비스 중지")
                applicationContext.contentResolver.unregisterContentObserver(contentObserver)
                stopForeground(true)
                stopSelf()
            }
        }
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        Log.d("Photo-service", "포토 서비스 삭제")
        createNotificationForExitPhotoService()
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        NotificationManagerCompat.from(this).notify(NOTIFICATION_ID, createNotificationForExitPhotoService())
        applicationContext.contentResolver.unregisterContentObserver(contentObserver)
        handlerThread.quitSafely()
        super.onDestroy()
    }

    private fun registerContentObserver() {
        handlerThread = HandlerThread("PhotoServiceBackground")
        handlerThread.start()
        backgroundHandler = Handler(handlerThread.looper)

        val processedUris = mutableSetOf<String>()

        contentObserver = object : ContentObserver(backgroundHandler) {
            @SuppressLint("Recycle")
            override fun onChange(selfChange: Boolean, uri: Uri?) {
                super.onChange(selfChange, uri)
                val uriString = uri?.toString() ?: return

                if (processedUris.contains(uriString)) {
                    return
                }else{
                    Log.d("CONTENT-OBSERVER", "$uri")
                    processedUris.add(uriString)
                    ioScope.launch {
                        try{
                            var isPending = 1
                            while(isPending == 1){
                                Log.d("CONTENT-OBSERVER", "isPending==1")
                                getUriCursor(uri)?.use{isPending = getIsPending(it)}
                                delay(500)
                            }
                            Log.d("CONTENT-OBSERVER", "isPending==0 확인")
                            val inputStream = applicationContext.contentResolver.openInputStream(uri)

                            val realFilePath = getPathFromUri(applicationContext, uri)
                            if (realFilePath != null && realFilePath.contains("Download/lovestory")) { return@launch }

                            val exifInterface = inputStream?.let { androidx.exifinterface.media.ExifInterface(it) }

                            val uriItemInfo =
                                getInfoFromImage(exifInterface = exifInterface) ?: return@launch
                            val photoId = getUriMD5Hash(uri = uri)

                            val photoForSyncDao = photoDatabase.photoForSyncDao()

                            val newPhoto = PhotoForSync(photoId!!, uriItemInfo.dateTime, uri.toString(), uriItemInfo.latitude, uriItemInfo.longitude)
                            photoForSyncDao.insertPhoto(newPhoto)
                            Log.d("CONTENT-OBSERVER", "db 추가 성공")
                        }
                        catch (e: InterruptedException) {
                            e.printStackTrace()
                        }
                        catch(e : Exception){
                            Log.e("CONTENT-OBSERVER", "$e")
                        }
                    }

                }
            }
        }
        applicationContext.contentResolver.registerContentObserver(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true,contentObserver
        )
    }

    fun getPathFromUri(context: Context, uri: Uri): String? {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        context.contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                return cursor.getString(columnIndex)
            }
        }
        return null
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, "Lovestory", importance)
            val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNotificationForPhotoService():Notification{
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("LoveStory")
            .setContentText("연인과의 추억을 기록합니다\n행복한 시간 되세요（＾∀＾●）ﾉｼ")
            .setSmallIcon(R.mipmap.ic_notification_foreground)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true).build()
    }

    private fun createNotificationForExitPhotoService():Notification{
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("LoveStory")
           .setContentText("오늘도 즐거운 데이트 하셨나요?\n앱을 실행해서 오늘의 추억을 확인해보세요")
            .setSmallIcon(R.mipmap.ic_notification_foreground)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
    }

    private fun getUriCursor(uri: Uri): Cursor? {
        val projection = arrayOf(MediaStore.Images.Media.IS_PENDING)
        return contentResolver.query(uri, projection, null, null, null)
    }

    private fun getIsPending(cursor: Cursor):Int{
        val pending= cursor.getColumnIndexOrThrow(MediaStore.Images.Media.IS_PENDING)
        cursor.moveToNext()
        return cursor.getInt(pending)
    }

    private fun getUriMD5Hash(uri: Uri): String? {
        val md5Hash = MessageDigest.getInstance("MD5")

        return try {
            val uriString = uri.toString()
            md5Hash.update(uriString!!.toByteArray())
            val result = BigInteger(1, md5Hash.digest()).toString(16)

            result
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    companion object {
        private const val CHANNEL_ID = "Lovestory"
        private const val NOTIFICATION_ID = 1
    }

}