package com.lovestory.lovestory.database.repository

import androidx.lifecycle.LiveData
import com.lovestory.lovestory.database.entities.PhotoForSync
import com.lovestory.lovestory.database.entities.PhotoForSyncDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PhotoForSyncRepository(private val photoForSyncDao: PhotoForSyncDao) {
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    val getAllPhotosForSync : LiveData<List<PhotoForSync>> = photoForSyncDao.getAll()

    fun insertPhotoForSync(item : PhotoForSync){coroutineScope.launch{photoForSyncDao.insertPhoto(photo = item)}}
    fun deletePhotoForSync(item : PhotoForSync){coroutineScope.launch{photoForSyncDao.deletePhoto(photo = item)}}

    suspend fun getPhotoForSyncById(id : String): PhotoForSync? {return withContext(Dispatchers.IO){ photoForSyncDao.getPhotoById(id) } }
}