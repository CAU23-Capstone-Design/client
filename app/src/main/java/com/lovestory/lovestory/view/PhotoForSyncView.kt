package com.lovestory.lovestory.view

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.lovestory.lovestory.database.PhotoDatabase
import com.lovestory.lovestory.database.entities.PhotoForSync
import com.lovestory.lovestory.database.repository.PhotoForSyncRepository

class PhotoForSyncView(application : Application) : ViewModel() {
    lateinit var listOfPhotoForSync : LiveData<List<PhotoForSync>>
    private lateinit var photoForSyncRepository: PhotoForSyncRepository

    init {
        val photoDatabase = PhotoDatabase.getDatabase(application)
        val photoForSyncDao = photoDatabase.photoForSyncDao()
        photoForSyncRepository = PhotoForSyncRepository(photoForSyncDao)

        listOfPhotoForSync = photoForSyncRepository.getAllPhotosForSync
    }

    var isUploadPhotos = false
    var currentUploadPhotos = 0
    var totalUploadPhotos = 0

    fun setUploadPhotos(numOfPhotos : Int){
        Log.d("VIEW-PhotoViewModel", "setUplaodPhotos 호출됨")
        isUploadPhotos = true
        totalUploadPhotos = numOfPhotos
    }

    fun addCurrentUploadPhotos(){
        if(isUploadPhotos){
            currentUploadPhotos += 1
        }
    }

    fun setFinishedUploadPhotos(){
        isUploadPhotos = false
        currentUploadPhotos = 0
        totalUploadPhotos = 0
    }
}