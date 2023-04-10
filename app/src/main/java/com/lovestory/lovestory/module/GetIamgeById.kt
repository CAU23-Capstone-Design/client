package com.lovestory.lovestory.module

import android.content.ContentValues
import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import com.lovestory.lovestory.database.PhotoDatabase
import com.lovestory.lovestory.network.getNotSyncImage
import com.lovestory.lovestory.repository.PhotoRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

suspend fun getImageById(context: Context ,token : String, photo_id : String){
    Log.d("MODULE-getImageById", "호출됨")
    lateinit var repository : PhotoRepository
    val localId = System.currentTimeMillis().toString()
    val destinationFile = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "$localId.jpg")

    CoroutineScope(Dispatchers.IO).launch {
        Log.d("MODULE-getImageById", "파일 다운받기 시도")
        val response = getNotSyncImage(token, photo_id)
        if (response.isSuccessful) {
            Log.d("MODULE-getImageById", "파일 다운받기 성공")
            val responseBody = response.body()!!
            val fileName = "image.jpg" // 이미지 파일 이름을 지정하세요.
            val imageFile = saveImageToLoveStoryFolderQPlus(context, responseBody, localId)
            Log.d("MODULE-getImageById", "파일 저장 완료 $imageFile")
        } else {
            Log.e("ERROR", "Failed to download image")
        }
//        val response = getNotSyncImage(token = token, photo_id= photo_id)
//        if (response.isSuccessful) {
//            val inputStream = response.body()?.byteStream()
//            inputStream?.let {
//                val lovestoryFolder = File(context.getExternalFilesDir(null),"lovestory")
//                Log.d("MODULE-getImageById", "$lovestoryFolder")
//                Log.d("MODULE-getImageById", "$destinationFile")
//                if (!lovestoryFolder.exists()) {
//                    lovestoryFolder.mkdirs()
//                }
//                val imageFile = File(lovestoryFolder, "$localId.jpg")
//
////                saveMediaFile(context = context, fileName = localId, inputStream = inputStream)
//                saveInputStreamToFile(inputStream, imageFile)
//            }
//        } else {
//            Log.e("DownloadImage", "Failed to download image: ${response.errorBody()}")
//        }
//        if(response.isSuccessful){
//            val photoDatabase = PhotoDatabase.getDatabase(context)
//            val photoDao = photoDatabase.photoDao()
//            repository = PhotoRepository(photoDao)
//
//            response.body()?.byteStream()?.use { inputStream ->
//                destinationFile.outputStream().use { outputStream ->
//                    inputStream.copyTo(outputStream)
//                }
//            }
//

//            val imageByteArray = response.body().bytes()
//
//            val storageDir = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "lovestory")
//            if (!storageDir.exists()) {
//                storageDir.mkdirs()
//            }
//
//            // 이미지를 저장할 파일을 만듭니다.
//            val file = File(storageDir, System.currentTimeMillis().toString())
//
//            // 파일에 이미지 데이터를 쓰기 위한 OutputStream을 생성합니다.
//            val outputStream = FileOutputStream(file)
//
//            outputStream.use { outputStream ->
//                // 이미지 데이터를 파일에 쓰고 저장합니다.
//                outputStream.write(imageByteArray)
//            }
//
//            // 저장된 파일의 content Uri를 반환합니다.
//            val contentUri = FileProvider.getUriForFile(context, context.packageName + ".provider", file)
//
//        }else{
//            Log.e("MODULE-getImageById", "${response.errorBody()}")
//        }
    }
}

@RequiresApi(Build.VERSION_CODES.Q)
suspend fun saveImageToLoveStoryFolderQPlus(
    context: Context,
    responseBody: ResponseBody,
    fileName: String
): Uri {
    val contentValues = ContentValues().apply {
        put(MediaStore.Downloads.DISPLAY_NAME, fileName)
        put(MediaStore.Downloads.MIME_TYPE, "image/jpeg")
        put(MediaStore.Downloads.RELATIVE_PATH, "${Environment.DIRECTORY_DOWNLOADS}/lovestory")
        put(MediaStore.Downloads.IS_PENDING, 1)
    }

    val resolver = context.contentResolver
    val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)

    uri?.let { targetUri ->
        resolver.openOutputStream(targetUri).use { outputStream ->
            responseBody.byteStream().use { inputStream ->
                inputStream.copyTo(outputStream!!)
            }
        }

        contentValues.clear()
        contentValues.put(MediaStore.Downloads.IS_PENDING, 0)
        resolver.update(targetUri, contentValues, null, null)
    }

    return uri ?: throw IOException("Failed to create new MediaStore record.")
}

fun createLoveStoryFolder(context: Context): File {
    val downloadsFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
    val loveStoryFolder = File(downloadsFolder, "lovestory")
    if (!loveStoryFolder.exists()) {
        loveStoryFolder.mkdirs()
    }
    return loveStoryFolder
}

suspend fun saveImageToLoveStoryFolder(context: Context, responseBody: ResponseBody, fileName: String): File {
    val loveStoryFolder = createLoveStoryFolder(context)
    val imageFile = File(loveStoryFolder, fileName)
    val inputStream = responseBody.byteStream()
    val outputStream = FileOutputStream(imageFile)

    inputStream.use { input ->
        outputStream.use { output ->
            input.copyTo(output)
        }
    }

    return imageFile
}


private fun saveInputStreamToFile(inputStream: InputStream, outputFile: File) {
    try {
        val outputStream = FileOutputStream(outputFile)
        inputStream.use { input ->
            outputStream.use { output ->
                input.copyTo(output)
            }
        }
        Log.d("MODULE-saveInputStreamToFile", "파일 저장 성공")

    } catch (e: IOException) {
        Log.e("SaveImage", "Failed to save image: $e")
    }
}


@RequiresApi(Build.VERSION_CODES.Q)
fun saveMediaFile(context: Context, fileName: String, inputStream: InputStream): Boolean {

    try {
        Log.d("MODULE-saveMediaFile", "이미지 저장 시작")
        val contentValues = ContentValues()
        contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/*");
        contentValues.put(MediaStore.Images.Media.IS_PENDING, 1)

        val contentResolver = context.contentResolver
        val collectionUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        val fileDescriptor = collectionUri?.let { contentResolver.openFileDescriptor(it, "w", null) }
            ?: return false
        val fileOutputStream = FileOutputStream(fileDescriptor.fileDescriptor)
        val buffer = ByteArray(1024)
        var read: Int
        while (inputStream.read(buffer).also { read = it } != -1) {
            fileOutputStream.write(buffer, 0, read)
        }
        inputStream.close()
        fileOutputStream.close()
        fileDescriptor.close()

        contentValues.clear()
        contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
        contentResolver.update(collectionUri, contentValues, null, null)
        Log.d("MODULE-saveMediaFile", "이미지 저장 성공")
        return true
    } catch (e: Exception) {
        e.printStackTrace()
        return false
    }
}
