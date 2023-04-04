package com.lovestory.lovestory.services

import android.Manifest
import android.app.*
import android.content.*
import android.content.pm.PackageManager
import android.database.ContentObserver
import android.media.ExifInterface
import android.net.Uri
import android.os.*
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.lovestory.lovestory.R
import com.lovestory.lovestory.broadcasts.LocationToPhoto.ACTION_START_PHOTO_PICKER_SERVICE
import com.lovestory.lovestory.broadcasts.LocationToPhoto.ACTION_STOP_PHOTO_PICKER_SERVICE
import java.io.IOException

class PhotoService : Service(){
    private lateinit var contentObserver: ContentObserver
    private lateinit var backgroundHandler: Handler
    private lateinit var handlerThread: HandlerThread
    val contextLocal = this

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

    override fun onCreate() {
        super.onCreate()
        Log.d("Photo-service", "포토 서비스 생성")
        createNotificationChannel()
    }

    override fun onDestroy() {
        Log.d("Photo-service", "포토 서비스 삭제")
        applicationContext.contentResolver.unregisterContentObserver(contentObserver)
        handlerThread.quitSafely()
        super.onDestroy()
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

    private fun registerContentObserver() {
        handlerThread = HandlerThread("PhotoServiceBackground")
        handlerThread.start()
        backgroundHandler = Handler(handlerThread.looper)

        val processedUris = mutableSetOf<String>()

        contentObserver = object : ContentObserver(backgroundHandler) {
            override fun onChange(selfChange: Boolean, uri: Uri?) {

                super.onChange(selfChange, uri)
                val uriString = uri?.toString() ?: return

                if (processedUris.contains(uriString)) {
                    return
                }else{
                    Log.d("CONTENT-Observer", "$uri")
//                    getRotationFromImageUri(
//                        uri = uri,
//                        context = contextLocal
//                    )
                    takePersistableUriPermission(contextLocal, uri)
//                    val filepath = getImageFilePath(contentResolver, uri)
//                    Log.d("COTENT-Observer-Uri-path", "$filepath")
//                    getPhotoInfo(uri)
//                    getPhotoLocation(uri, contextLocal)

                    processedUris.add(uriString)
//
//                    val contentResolver = contentResolver
//                    val inputStream = applicationContext.contentResolver.openInputStream(uri)
//                    val exifInterface = inputStream?.let { ExifInterface(it) }
//
//                    val orientation = exifInterface?.getAttribute(ExifInterface.TAG_ORIENTATION)
//                    val dateTaken = exifInterface?.getAttribute(ExifInterface.TAG_DATETIME)
//
//                    Log.e("dsfgsdj.kfbgjzdbfgjkbfdg", "$orientation &&&& $dateTaken")
                }
            }
        }

        applicationContext.contentResolver.registerContentObserver(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            true,
            contentObserver
        )
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    fun takePersistableUriPermission(context: Context, uri: Uri) {
        Log.d("takePersistableUriPermission", "진앱")
        if (DocumentsContract.isDocumentUri(context, uri)) {
            Log.d("takePersistableUriPermission", "진앱ㄴㅇㄹㄴㅇㄹㄴㅇ")
            val takeFlags: Int = Intent.FLAG_GRANT_READ_URI_PERMISSION or
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION

            context.contentResolver.takePersistableUriPermission(uri, takeFlags)
            getRotationFromImageUri(
                        uri = uri,
                        context = contextLocal
                    )
        }
        val takeFlags: Int = Intent.FLAG_GRANT_READ_URI_PERMISSION or
                Intent.FLAG_GRANT_WRITE_URI_PERMISSION

        context.contentResolver.takePersistableUriPermission(uri, takeFlags)
        getRotationFromImageUri(
            uri = uri,
            context = contextLocal
        )
    }

    private fun getPhotoInfo(uri: Uri){
        val projection = arrayOf(
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATE_ADDED,
            MediaStore.Images.Media.GENERATION_ADDED,
            MediaStore.Images.Media.TITLE,
//            MediaStore.Images.Media.XMP,
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.IS_PENDING,
            MediaStore.Images.Media.IS_TRASHED
        )
        val cursor = contentResolver.query(uri, projection, null, null, null)
        cursor?.use{cursor->
            val filePath = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            val fileName = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
            val fileDateAdd = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED)
            val fileGenerationAdd = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.GENERATION_ADDED)
            val fileTitle = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.TITLE)
//            val fileXmp = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.XMP)
            val imageId = cursor.getColumnIndex(MediaStore.Images.Media._ID)


            while(cursor.moveToNext()){
                val path = cursor.getString(filePath)
                val name = cursor.getString(fileName)
                val dateAdd = cursor.getString(fileDateAdd)
                val generationAdd = cursor.getString(fileGenerationAdd)
                val title = cursor.getString(fileTitle)
//                val xmp = cursor.getString(fileXmp)
                val idid = cursor.getLong(imageId)

                Log.d("Image Get", "$path - $name ")
                Log.d("Image Get2", "$dateAdd - $generationAdd")
                Log.d("Image Get3", "$title ")

                val imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, idid)
                Log.d("Image get4", "$idid - $imageUri")
                val testuri = Uri.parse(path)
                getPhotoLocation(context = this, filePath = testuri)
            }
        }
    }
}

fun getRotationFromImageUri(context: Context, uri: Uri) {
    val inputStream = context.contentResolver.openInputStream(uri)
    val exifInterface = inputStream?.let { androidx.exifinterface.media.ExifInterface(it) }

    val orientation = exifInterface?.getAttribute(androidx.exifinterface.media.ExifInterface.TAG_ORIENTATION)
    val dateTaken = exifInterface?.getAttribute(androidx.exifinterface.media.ExifInterface.TAG_DATETIME)
    val latitude = exifInterface?.getAttribute(android.media.ExifInterface.TAG_GPS_LATITUDE)
    val longitude = exifInterface?.getAttribute(android.media.ExifInterface.TAG_GPS_LONGITUDE)

    Log.e("dsfgsdj.kfbgjzdbfgjkbfdg", "$orientation &&&& $dateTaken")
    Log.d("getPhotoeExif", "$latitude - $longitude")

}

@Composable
fun getReadMediaImagePermission(){
    val context = LocalContext.current
//    val permission = Manifest.permission.READ_MEDIA_IMAGES
//    val permissionResult =  ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED
//
//    val externalStoragePermissionRequest = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.RequestPermission()
//    ){ granted->
//        Log.d("getExternalStorage-Permission", "$granted")
//        if(granted){
//
//        }else{
//            Toast.makeText(context, "LoveStory에서 사진에 접근할 수 있도록 권한을 허용해주세요", Toast.LENGTH_SHORT).show()
//        }
//    }
//
//    if(permissionResult){
//        SideEffect {
//            externalStoragePermissionRequest.launch(permission)
//        }
//    }

    val permission = arrayOf(Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_VIDEO, Manifest.permission.READ_MEDIA_AUDIO, Manifest.permission.READ_EXTERNAL_STORAGE)
    val permissionResult = ContextCompat.checkSelfPermission(context, permission[0]) != PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(context, permission[1]) != PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(context, permission[2]) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(context, permission[3]) != PackageManager.PERMISSION_GRANTED

    val locationPermissionRequest = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ){permissions ->
        when{
            permissions.getOrDefault(Manifest.permission.READ_MEDIA_IMAGES, false) -> {
                Toast.makeText(context, "정확한 위sdfsdfsdfsdfds으로 설정해주세요", Toast.LENGTH_SHORT).show()
            }
            permissions.getOrDefault(Manifest.permission.READ_MEDIA_VIDEO, false) -> {
                Toast.makeText(context, "정확한 위fvcxbnbfbdfn위치 권한으로 설정해주세요", Toast.LENGTH_SHORT).show()
            }
            permissions.getOrDefault(Manifest.permission.READ_MEDIA_AUDIO, false) -> {
                Toast.makeText(context, "정확한 위치ewrwetg34f 권한으로 설정해주세요", Toast.LENGTH_SHORT).show()
            }
            permissions.getOrDefault(Manifest.permission.READ_EXTERNAL_STORAGE, false) -> {
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
fun getExternalStoragePermission(){
    Log.d("getExternalStorage-Permission", "권한체크")
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
            shouldShowRequestPermissionRationale(context as Activity,Manifest.permission.READ_EXTERNAL_STORAGE)
            externalStoragePermissionRequest.launch(permission)
        }
    }
//    Log.d("getExternalStorage-Permission", "wpqkfsdfsdfsdf")
//    ActivityCompat.requestPermissions(
//        context as Activity,
//        arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
//        100
//    )
}

fun getPhotoLocation(filePath: Uri, context : Context): Pair<String?, String?> {
    var latitude: String? = null
    var longitude: String? = null

    try {
        val inputStream = context.contentResolver.openInputStream(filePath)
        val exifInterface = inputStream?.let { ExifInterface(it) }


        val hasLatitude = exifInterface?.hasAttribute(ExifInterface.TAG_GPS_LATITUDE) ?: false
        val hasLongitude = exifInterface?.hasAttribute(ExifInterface.TAG_GPS_LONGITUDE) ?: false

        if (hasLatitude && hasLongitude) {
            latitude = exifInterface?.getAttribute(ExifInterface.TAG_GPS_LATITUDE)
            longitude = exifInterface?.getAttribute(ExifInterface.TAG_GPS_LONGITUDE)
        }

        Log.d("getPhotoeExif", "$hasLatitude - $hasLongitude")

//        Log.d("getPhotoeExif", "$result1 - $result2")

    } catch (e: IOException) {
        Log.e("Photo Location", "Error reading Exif data", e)
    }

    Log.d("getPhotoeExif", "$latitude - $longitude")
    return Pair(latitude, longitude)
}

fun convertDMSToDecimal(dms: String?): Double? {
    if (dms == null) return null
    val dmsSplit = dms.split(",", limit = 3)
    val degrees = dmsSplit[0].split("/").map { it.toDouble() }.reduce { a, b -> a / b }
    val minutes = dmsSplit[1].split("/").map { it.toDouble() }.reduce { a, b -> a / b }
    val seconds = dmsSplit[2].split("/").map { it.toDouble() }.reduce { a, b -> a / b }
    return degrees + minutes / 60 + seconds / 3600
}

