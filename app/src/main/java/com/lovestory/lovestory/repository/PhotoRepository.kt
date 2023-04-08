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
    val notSyncedPhotos :LiveData<List<Photo>> = photoDao.getNotSyncedPhotos()
    val syncedPhotos : LiveData<List<Photo>> =photoDao.getSyncedPhotos()

    suspend fun isPhotoExistById(photo_id: String): Boolean {
        return photoDao.getPhotoById(id = photo_id) != null
    }

    fun updatePhotoSyncStatusAndLocation(photoId: String, location: String) {
        coroutineScope.launch(Dispatchers.IO) {
            photoDao.updatePhotoSyncStatusAndLocationById(photoId, location)
        }
    }


//
//    fun insertPhoto(photo : Photo){
//        coroutineScope.launch(Dispatchers.IO) {
//            photoDao.insertPhoto(photo = photo)
//        }
//    }
//
//    fun updatePhoto(photo: Photo){
//        coroutineScope.launch(Dispatchers.IO) {
//            photoDao.updatePhoto(photo = photo)
//        }
//    }
//
//    fun getListRequestDataPhotos(requestDate : String){
//        coroutineScope.launch(Dispatchers.IO){
//            photoDao.getRequestDatePhotos(requestDate = requestDate)
//        }
//    }
//
//    fun isExistPhotoById(photo_id : String){
//        coroutineScope.launch(Dispatchers.IO){
//            photoDao.getPhotoById(id = photo_id)
//        }
//    }
}

