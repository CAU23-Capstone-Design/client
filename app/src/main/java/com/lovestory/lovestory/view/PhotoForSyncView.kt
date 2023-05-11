package com.lovestory.lovestory.view

import android.app.Application
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.lovestory.lovestory.database.PhotoDatabase
import com.lovestory.lovestory.database.entities.PhotoForSync
import com.lovestory.lovestory.database.repository.PhotoForSyncRepository

class PhotoForSyncView(application : Application) : ViewModel() {
    lateinit var listOfPhotoForSync : LiveData<List<PhotoForSync>>

    private lateinit var photoForSyncRepository: PhotoForSyncRepository

    var checkPhotoList = mutableStateOf(listOf<Boolean>())

    init {
        val photoDatabase = PhotoDatabase.getDatabase(application)
        val photoForSyncDao = photoDatabase.photoForSyncDao()
        photoForSyncRepository = PhotoForSyncRepository(photoForSyncDao)

        listOfPhotoForSync = photoForSyncRepository.getAllPhotosForSync
    }
}