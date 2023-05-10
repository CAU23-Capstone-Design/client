package com.lovestory.lovestory.module

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.navigation.NavHostController
import com.lovestory.lovestory.database.PhotoDatabase
import com.lovestory.lovestory.database.entities.PhotoForSync
import com.lovestory.lovestory.database.entities.PhotoForSyncDao
import com.lovestory.lovestory.database.entities.SyncedPhoto
import com.lovestory.lovestory.database.entities.SyncedPhotoDao
import com.lovestory.lovestory.database.repository.PhotoForSyncRepository
import com.lovestory.lovestory.database.repository.SyncedPhotoRepository
import com.lovestory.lovestory.network.uploadPhotoToServer
import com.lovestory.lovestory.view.PhotoForSyncView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody

suspend fun uploadPhoto(
    context : Context,
    sendPhotos : List<PhotoForSync>,
    numOfCurrentUploadedPhoto : MutableState<Int>,
){
    val photoDatabase: PhotoDatabase = PhotoDatabase.getDatabase(context)

    val photoForSyncDao : PhotoForSyncDao = photoDatabase.photoForSyncDao()
    val syncedPhotoDao : SyncedPhotoDao = photoDatabase.syncedPhotoDao()

    val photoForSyncRepository = PhotoForSyncRepository(photoForSyncDao)
    val syncedPhotoRepository = SyncedPhotoRepository(syncedPhotoDao)

    val token = getToken(context)

    sendPhotos.onEachIndexed{ index, photo ->
        Log.d("MODULE-uploadPhoto", "Uri : ${photo.imageUrl}")
        val uri = Uri.parse(photo.imageUrl)
        Log.d("MODULE-uploadPhoto", "Uri : $uri")

        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            Log.d("MODULE-uploadPhoto", "사진 불러오기")
            val byteArray = inputStream.readBytes()

            val requestFile = byteArray?.let {
                RequestBody.create("image/*".toMediaTypeOrNull(), it)
            }
            val imagePart = requestFile?.let {
                MultipartBody.Part.createFormData("image", "${photo.id}.jpg", it)
            }

            val response = withContext(Dispatchers.IO) {
                uploadPhotoToServer(token!!, imagePart!!, photo.id)
            }
            Log.d("MODULE-uploadPhoto", "사진 업로드 실행")
            if(response.isSuccessful){
                Log.d("MODULE-uploadPhoto", "${response.body()}")

                syncedPhotoRepository.insertSyncedPhoto(
                    SyncedPhoto(
                        id = response.body()!!.local_id,
                        date = response.body()!!.date,
                        area1 = response.body()!!.location.area1,
                        area2 = response.body()!!.location.area2,
                        area3 = response.body()!!.location.area3,
                        latitude = response.body()!!.latitude,
                        longitude = response.body()!!.longitude
                    )
                )

                photoForSyncRepository.deletePhotoForSync(photo)
                numOfCurrentUploadedPhoto.value+=1

            }else{
                Log.e("MODULE-uploadPhoto" , "${response.errorBody()}")
            }
        }
    }
}