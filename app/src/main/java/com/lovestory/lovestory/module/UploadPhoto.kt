package com.lovestory.lovestory.module

import android.content.Context
import android.net.Uri
import android.util.Log
import com.lovestory.lovestory.database.PhotoDatabase
import com.lovestory.lovestory.entity.Photo
import com.lovestory.lovestory.network.uploadPhotoToServer
import com.lovestory.lovestory.repository.PhotoRepository
import com.lovestory.lovestory.view.PhotoView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

suspend fun uploadPhoto(context : Context, sendPhotos : List<Photo>, viewModel: PhotoView){
    Log.d("MODULE-uploadPhoto", "setUploadPhotos 호출")
    viewModel.setUploadPhotos(sendPhotos.size)
    val photoDatabase: PhotoDatabase = PhotoDatabase.getDatabase(context)
    val photoDao = photoDatabase.photoDao()
    val repository = PhotoRepository(photoDao)
    val token = getToken(context)

    for(photo in sendPhotos){
        Log.d("MODULE-uploadPhoto", "Uri : ${photo.imageUrl}")
        val uri = Uri.parse(photo.imageUrl)
        Log.d("MODULE-uploadPhoto", "Uri : $uri")
//        val compressedImageUri = compressImage(context, uri, 75)
//        val file = File(compressedImageUri!!.path)
//        val requestBody = RequestBody.create("image/*".toMediaTypeOrNull(), file)
//        val multipartBody = MultipartBody.Part.createFormData("image", file.name, requestBody)
//
//        val response = withContext(Dispatchers.IO) {
//            uploadPhotoToServer(token!!, multipartBody!!, photo.id)
//        }
//
//        Log.d("MODULE-uploadPhoto", "사진 업로드 실행")
//        Log.d("MODULE-uploadPhoto", "사진 ${response.body()}")
//        if(response.isSuccessful){
//            Log.d("MODULE-uploadPhoto", "${response.body()}")
//        }else{
//            Log.e("MODULE-uploadPhoto" , "${response.errorBody()}")
//        }
//        repository.updatePhotoSyncStatusAndLocation(
//            photoId = photo.id,
//            area1 = response.body()!!.location.area1,
//            area2= response.body()!!.location.area2,
//            area3= response.body()!!.location.area3
//        )

        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            Log.d("MODULE-uploadPhoto", "사진 불러오기")
            val byteArray = inputStream.readBytes()

            val requestFile = byteArray?.let {
                RequestBody.create("image/*".toMediaTypeOrNull(), it)
            }
            val imagePart = requestFile?.let {
                MultipartBody.Part.createFormData("image", "image.jpg", it)
            }

            val response = withContext(Dispatchers.IO) {
                uploadPhotoToServer(token!!, imagePart!!, photo.id)
            }
            Log.d("MODULE-uploadPhoto", "사진 업로드 실행")
            if(response.isSuccessful){
                Log.d("MODULE-uploadPhoto", "${response.body()}")
            }else{
                Log.e("MODULE-uploadPhoto" , "${response.errorBody()}")
            }
            repository.updatePhotoSyncStatusAndLocation(
                photoId = photo.id,
                area1 = response.body()!!.location.area1,
                area2= response.body()!!.location.area2,
                area3= response.body()!!.location.area3
            )
        }
        viewModel.addCurrentUploadPhotos()
    }
    viewModel.setFinishedUploadPhotos()
}