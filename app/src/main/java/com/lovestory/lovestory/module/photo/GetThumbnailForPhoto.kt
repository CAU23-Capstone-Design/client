package com.lovestory.lovestory.module.photo

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.lovestory.lovestory.network.getThumbnailById
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun getThumbnailForPhoto(token:String, photo_id : String):Bitmap?{
    return  withContext(Dispatchers.IO){
        val response = getThumbnailById(token, photo_id)
        if(response.isSuccessful){
            val responseBody = response.body()!!
            try{
                BitmapFactory.decodeStream(responseBody.byteStream())
            }catch (e: Exception) {
                null
            }
        }
        else{
            null
        }
    }
}