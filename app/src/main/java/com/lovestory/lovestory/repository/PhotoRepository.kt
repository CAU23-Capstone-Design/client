package com.lovestory.lovestory.repository

import com.lovestory.lovestory.entity.Photo
import com.lovestory.lovestory.entity.PhotoDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PhotoRepository(private val photoDao: PhotoDao) {
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    fun insertPhoto(photo : Photo){
        coroutineScope.launch(Dispatchers.IO) {
            photoDao.insertPhoto(photo = photo)
        }
    }
}