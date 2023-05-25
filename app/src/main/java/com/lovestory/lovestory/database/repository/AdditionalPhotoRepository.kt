package com.lovestory.lovestory.database.repository

import android.util.Log
import androidx.lifecycle.LiveData
import com.lovestory.lovestory.database.entities.AdditionalPhoto
import com.lovestory.lovestory.database.entities.AdditionalPhotoDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AdditionalPhotoRepository(private val additionalPhotoDao: AdditionalPhotoDao) {
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    val getAdditionalPhotoDao : LiveData<List<AdditionalPhoto>> = additionalPhotoDao.getAll()

    fun insertAdditionalPhoto(item : AdditionalPhoto){coroutineScope.launch { additionalPhotoDao.insertPhoto(item) }}
    fun deleteAdditionalPhoto(item : AdditionalPhoto){coroutineScope.launch { additionalPhotoDao.deletePhoto(item) }}

    fun deleteAllAdditionalPhoto(){coroutineScope.launch { additionalPhotoDao.deleteAll() }}

    fun getAdditionalPhotoById(id : String): AdditionalPhoto?{
       Log.d("Respository","${additionalPhotoDao.getPhotoById(id)}")
        return additionalPhotoDao.getPhotoById(id)
    }
}