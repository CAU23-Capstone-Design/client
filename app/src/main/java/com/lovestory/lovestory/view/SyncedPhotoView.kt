package com.lovestory.lovestory.view

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.lovestory.lovestory.database.PhotoDatabase
import com.lovestory.lovestory.database.entities.SyncedPhoto
import com.lovestory.lovestory.database.repository.SyncedPhotoRepository

class SyncedPhotoView(application:Application): ViewModel() {
    lateinit var listOfSyncPhotos : LiveData<List<SyncedPhoto>>
    private lateinit var syncedPhotoRepository: SyncedPhotoRepository

    init {
        val photoDatabase = PhotoDatabase.getDatabase(application)
        val syncedPhotoDao =  photoDatabase.syncedPhotoDao()
        syncedPhotoRepository = SyncedPhotoRepository(syncedPhotoDao)

        listOfSyncPhotos = syncedPhotoRepository.getAllSyncedPhotos
    }
}