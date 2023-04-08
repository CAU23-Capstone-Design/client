package com.lovestory.lovestory.view

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.lovestory.lovestory.database.PhotoDatabase
import com.lovestory.lovestory.entity.Photo
import com.lovestory.lovestory.repository.PhotoRepository

class PhotoViewModel(application : Application) : ViewModel() {
    lateinit var allPhotos: LiveData<List<Photo>>
    lateinit var syncedPhotos : LiveData<List<Photo>>
    lateinit var notSyncedPhotos : LiveData<List<Photo>>
    private lateinit var repository : PhotoRepository

    init {
        val photoDatabase = PhotoDatabase.getDatabase(application)
        val photoDao = photoDatabase.photoDao()
        repository = PhotoRepository(photoDao)

        allPhotos = repository.allPhotos
        syncedPhotos = repository.syncedPhotos
        notSyncedPhotos = repository.notSyncedPhotos

    }
}