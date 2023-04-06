package com.lovestory.lovestory.module

import android.content.Context
import android.database.ContentObserver
import android.net.Uri
import android.util.Log
import com.lovestory.lovestory.entity.Photo
import com.lovestory.lovestory.network.sendLocationToServer
import com.lovestory.lovestory.network.uploadPhotoToServer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody

fun uploadPhoto(context : Context, sendPhotos : List<Photo>){
    val token = getToken(context)

    for(photo in sendPhotos){
        val uri = Uri.parse(photo.imageUrl)
        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            val byteArray = inputStream.readBytes()

            val requestFile = byteArray?.let {
                RequestBody.create("image/*".toMediaTypeOrNull(), it)
            }
            val imagePart = requestFile?.let {
                MultipartBody.Part.createFormData("image", "image.jpg", it)
            }

            CoroutineScope(Dispatchers.IO).launch{
                val response = uploadPhotoToServer(token!!, imagePart!!, photo.id)
                if(response.isSuccessful){
                    Log.d("Upload Img", "${response.body()}")
                }else{
                    Log.e("upload Img error" , "${response.errorBody()}")
                }
            }

        }
    }
}
