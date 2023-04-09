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

    fun updatePhotoSyncStatusAndLocation(photoId: String, area1 : String, area2 : String, area3 : String) {
        coroutineScope.launch(Dispatchers.IO) {
            photoDao.updatePhotoSyncStatusAndLocationById(photoId, area1 = area1, area2 = area2, area3 = area3)
        }
    }
}

