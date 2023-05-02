package com.lovestory.lovestory.database.repository

import androidx.lifecycle.LiveData
import com.lovestory.lovestory.database.entities.SyncedPhoto
import com.lovestory.lovestory.database.entities.SyncedPhotoDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SyncedPhotoRepository(private val syncedPhotoDao: SyncedPhotoDao) {
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    val getAllSyncedPhotos : LiveData<List<SyncedPhoto>> = syncedPhotoDao.getAll()

    fun insertSyncedPhoto(item : SyncedPhoto){coroutineScope.launch { syncedPhotoDao.insert(item) }}
    fun deleteSyncedPhoto(item : SyncedPhoto){coroutineScope.launch { syncedPhotoDao.delete(item) }}

    suspend fun getSyncedPhotosByDate(targetDate : String): List<SyncedPhoto> {return withContext(Dispatchers.IO){syncedPhotoDao.getPhotosByDate(targetDate)} }
    suspend fun getSyncedPhotosByArea2(targetArea2 : String): List<SyncedPhoto> {return withContext(Dispatchers.IO){syncedPhotoDao.getPhotosByArea2(targetArea2)} }
    suspend fun getFirstSyncedPhotoByDate(targetDate : String): SyncedPhoto? {return withContext(Dispatchers.IO){syncedPhotoDao.getFirstPhotoByDate(targetDate)} }
    suspend fun getSyncedPhotoById(id : String): Boolean {return withContext(Dispatchers.IO){ syncedPhotoDao.getPhotoById(id) != null } }

}