package com.lovestory.lovestory.repository

import androidx.lifecycle.LiveData
import com.lovestory.lovestory.entity.Photo
import com.lovestory.lovestory.entity.PhotoDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PhotoRepository(private val photoDao: PhotoDao) {
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    val allPhotos : LiveData<List<Photo>> = photoDao.getAllPhotos()

    fun insertPhoto(photo : Photo){
        coroutineScope.launch(Dispatchers.IO) {
            photoDao.insertPhoto(photo = photo)
        }
    }

//    fun getAllPhotos(){
//        coroutineScope.launch(Dispatchers.IO) {
//            photoDao.getAllPhotos()
//        }
//    }
}