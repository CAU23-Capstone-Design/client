package com.lovestory.lovestory.module

import android.content.Context
import android.util.Log
import com.lovestory.lovestory.database.PhotoDatabase
import com.lovestory.lovestory.database.repository.PhotoForSyncRepository
import com.lovestory.lovestory.network.getPhotoTable
import com.lovestory.lovestory.view.ImageSyncView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

suspend fun checkExistNeedPhotoForSync(context : Context, imageSyncView : ImageSyncView){
    lateinit var repository : PhotoForSyncRepository
    val token = getToken(context)

    CoroutineScope(Dispatchers.IO).launch {
        val response = getPhotoTable(token!!)
        if(response.isSuccessful){
            val photoDatabase = PhotoDatabase.getDatabase(context)
            val photoDao = photoDatabase.photoForSyncDao()
            repository = PhotoForSyncRepository(photoDao)
            Log.d("MODULE-checkExistNeedPhotoForSync", "${response.body()}")
            for(local_id in response.body()!!){
                if(!repository.getPhotoForSyncById(local_id)){
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