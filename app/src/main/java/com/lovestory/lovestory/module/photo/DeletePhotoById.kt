package com.lovestory.lovestory.module.photo

import android.content.Context
import com.lovestory.lovestory.module.checkExistNeedPhotoForSync
import com.lovestory.lovestory.module.getToken
import com.lovestory.lovestory.network.deletePhotoById

suspend fun deletePhotosByIds(
    context : Context,
    listOfSelectedPhotos : Set<String>
){
    val token = getToken(context)

    listOfSelectedPhotos.forEach { photoIdForDelete->
        deletePhotoById(token!!, photoIdForDelete)
    }
    checkExistNeedPhotoForSync(context)
}