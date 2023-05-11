package com.lovestory.lovestory.module.photo

import android.content.Context
import android.util.Log
import com.lovestory.lovestory.module.checkExistNeedPhotoForSync
import com.lovestory.lovestory.module.getToken
import com.lovestory.lovestory.network.deletePhotoById
import com.lovestory.lovestory.view.SyncedPhotoView

suspend fun deletePhotosByIds(
    context : Context,
    syncedPhotoView : SyncedPhotoView
){
    val token = getToken(context)

    syncedPhotoView.selectedPhotosSet.value.forEach { photoIdForDelete->
        Log.d("[MODULE] deletePhotoByIds", "before remove : $photoIdForDelete")
        val result = deletePhotoById(token!!, photoIdForDelete)
        if(result.isSuccessful){
            Log.d("[MODULE] deletePhotoByIds", "success : ${result.body()}")
        }else{
            Log.e("[MODULE] deletePhotoByIds", "error : ${result.errorBody()}")
        }
    }
    syncedPhotoView.clearSelectedPhotosSet()
    checkExistNeedPhotoForSync(context)
}