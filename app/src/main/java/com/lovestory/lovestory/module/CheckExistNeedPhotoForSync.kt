package com.lovestory.lovestory.module

import android.content.Context
import android.util.Log
import com.lovestory.lovestory.database.PhotoDatabase
import com.lovestory.lovestory.network.getPhotoTable
import com.lovestory.lovestory.repository.PhotoRepository
import com.lovestory.lovestory.view.ImageSyncView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

suspend fun checkExistNeedPhotoForSync(context : Context, imageSyncView : ImageSyncView){
    lateinit var repository : PhotoRepository
    val token = getToken(context)

    CoroutineScope(Dispatchers.IO).launch {
        val response = getPhotoTable(token!!)
        if(response.isSuccessful){
            val photoDatabase = PhotoDatabase.getDatabase(context)
            val photoDao = photoDatabase.photoDao()
            repository = PhotoRepository(photoDao)
            Log.d("MODULE-checkExistNeedPhotoForSync", "${response.body()}")
            for(local_id in response.body()!!){
                if(!repository.isPhotoExistById(local_id)){
                    Log.d("MODULE-checkExistNeedPhotoForSync", "not existed $local_id")
                    imageSyncView.getImageFromServer(context, token, local_id)
                }
            }
        }
        else{
            Log.e("MODULE-checkExistNeedPhotoForSync", "${response.errorBody()}")
        }
    }
}