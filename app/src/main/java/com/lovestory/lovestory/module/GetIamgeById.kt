package com.lovestory.lovestory.module

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.core.content.FileProvider
import com.lovestory.lovestory.database.PhotoDatabase
import com.lovestory.lovestory.network.getNotSyncImage
import com.lovestory.lovestory.repository.PhotoRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

suspend fun getImageById(context: Context ,token : String, photo_id : String){
    lateinit var repository : PhotoRepository
    val localId = System.currentTimeMillis().toString()
    val destinationFile = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "$localId.jpg")

    CoroutineScope(Dispatchers.IO).launch {
        val response = getNotSyncImage(token = token, photo_id= photo_id)
        if (response.isSuccessful) {
            val inputStream = response.body()?.byteStream()
            inputStream?.let {
                val lovestoryFolder = File(context.getExternalFilesDir(null), "lovestory")
                if (!lovestoryFolder.exists()) {
                    lovestoryFolder.mkdirs()
                }
                val imageFile = File(lovestoryFolder, "$localId.jpg")
                saveInputStreamToFile(inputStream, imageFile)
            }
        } else {
            Log.e("DownloadImage", "Failed to download image: ${response.errorBody()}")
        }
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