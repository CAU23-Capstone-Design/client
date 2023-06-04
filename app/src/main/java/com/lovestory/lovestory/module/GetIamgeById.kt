package com.lovestory.lovestory.module

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.lovestory.lovestory.network.getNotSyncImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import kotlin.system.measureTimeMillis

suspend fun getImageById(context: Context ,token : String, photo_id : String){
    val localId = System.currentTimeMillis().toString()

    CoroutineScope(Dispatchers.IO).launch {
        Log.d("MODULE-getImageById", "파일 다운받기 시도")

        val response = getNotSyncImage(token, photo_id)
        if (response.isSuccessful) {
            Log.d("MODULE-getImageById", "파일 다운받기 성공")

            val responseBody = response.body()!!

            saveImageToLoveStoryFolderQPlus(context, responseBody, localId)
            Log.d("MODULE-getImageById", "파일 저장 완료 $localId")
        } else {
            Log.e("MODULE-getImageById", "Failed to download image")
        }
        withContext(Dispatchers.Main){
            Toast.makeText(context, "사진 다운로드 완료했습니다.", Toast.LENGTH_SHORT).show()
        }
    }
}

@RequiresApi(Build.VERSION_CODES.Q)
fun saveImageToLoveStoryFolderQPlus(
    context: Context,
    responseBody: ResponseBody,
    fileName: String
){
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
}