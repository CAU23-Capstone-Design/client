package com.lovestory.lovestory.module

import android.content.Context
import android.util.Log
import com.lovestory.lovestory.database.PhotoDatabase
import com.lovestory.lovestory.database.entities.PhotoForSync
import com.lovestory.lovestory.database.entities.SyncedPhoto
import com.lovestory.lovestory.database.repository.PhotoForSyncRepository
import com.lovestory.lovestory.database.repository.SyncedPhotoRepository
import com.lovestory.lovestory.network.getNotSyncImageInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

suspend fun getImageInfoById(context: Context, token : String, photo_id : String, photo_uri : String){
    Log.d("MODULE-getImageInfoById", "호출됨")
    val photoDatabase: PhotoDatabase = PhotoDatabase.getDatabase(context)
    val photoDao = photoDatabase.syncedPhotoDao()
    val repository = SyncedPhotoRepository(photoDao)

    CoroutineScope(Dispatchers.IO).launch{
        val response = getNotSyncImageInfo(token = token, photo_id = photo_id)
        if (response.isSuccessful){
            val imageInfo = response.body()!!

            Log.d("MODULE-getImageInfoById", "$imageInfo")

            repository.insertSyncedPhoto(
                SyncedPhoto(
                    id = imageInfo.local_id,
                    date = imageInfo.date,
                    latitude = imageInfo.latitude,
                    longitude = imageInfo.longitude,
                    area1 = imageInfo.location.area1,
                    area2 = imageInfo.location.area2,
                    area3 = imageInfo.location.area3,
                )
            )
        }else{
            Log.e("MODULE-getImageInfoById", "Fail get image info")
        }
    }


}