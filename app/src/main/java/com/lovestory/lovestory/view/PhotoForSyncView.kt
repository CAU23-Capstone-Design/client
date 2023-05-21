package com.lovestory.lovestory.view

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.lovestory.lovestory.database.PhotoDatabase
import com.lovestory.lovestory.database.entities.AdditionalPhoto
import com.lovestory.lovestory.database.entities.PhotoForSync
import com.lovestory.lovestory.database.repository.AdditionalPhotoRepository
import com.lovestory.lovestory.database.repository.PhotoForSyncRepository

class PhotoForSyncView(application : Application) : ViewModel() {
    lateinit var listOfPhotoForSync : LiveData<List<PhotoForSync>>
    lateinit var listOfAdditionPhotosForSync : LiveData<List<AdditionalPhoto>>

    private lateinit var photoForSyncRepository: PhotoForSyncRepository
    private lateinit var additionalPhotoRepository: AdditionalPhotoRepository

    var checkPhotoList = mutableStateOf(listOf<Boolean>())
//    var checkPhotoList = listOfPhotoForSync.value?.size?.let { MutableList<Boolean>(it) { true } }
    var checkPhotoFromGalleryList = mutableStateOf(listOf<Boolean>())

    init {
        val photoDatabase = PhotoDatabase.getDatabase(application)
        val photoForSyncDao = photoDatabase.photoForSyncDao()
        val additionalPhotoDao = photoDatabase.additionalPhotoDao()
        photoForSyncRepository = PhotoForSyncRepository(photoForSyncDao)
        additionalPhotoRepository = AdditionalPhotoRepository(additionalPhotoDao)

        listOfPhotoForSync = photoForSyncRepository.getAllPhotosForSync
        listOfAdditionPhotosForSync = additionalPhotoRepository.getAdditionalPhotoDao
    }
}