package com.lovestory.lovestory.module.photo

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.lovestory.lovestory.network.getDetailById
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun getDetailPhoto(token:String, photo_id : String): Bitmap?{
    return  withContext(Dispatchers.IO){
        val response = getDetailById(token, photo_id)
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