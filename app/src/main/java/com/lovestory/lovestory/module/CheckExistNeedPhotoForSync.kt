package com.lovestory.lovestory.module

import android.content.Context
import android.util.Log
import com.lovestory.lovestory.database.PhotoDatabase
import com.lovestory.lovestory.network.getPhotoTable
import com.lovestory.lovestory.repository.PhotoRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

suspend fun checkExistNeedPhotoForSync(context : Context){
    lateinit var repository : PhotoRepository
    val token = getToken(context)

    CoroutineScope(Dispatchers.IO).launch {
        val response = getPhotoTable(token!!)
        if(response.isSuccessful){
            val photoDatabase = PhotoDatabase.getDatabase(context)
            val photoDao = photoDatabase.photoDao()
            repository = PhotoRepository(photoDao)
            for(local_id in response.body()!!.photoList){
                if(!repository.isPhotoExistById(local_id)){
                    Log.d("MODULE-checkExistNeedPhotoForSync", "not existed $local_id")
                }
            }
        }
        else{
            Log.e("MODULE-checkExistNeedPhotoForSync", "${response.errorBody()}")
        }
    }
}