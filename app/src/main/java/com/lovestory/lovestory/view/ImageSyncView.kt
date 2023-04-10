package com.lovestory.lovestory.view

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lovestory.lovestory.module.getImageInfoById
import com.lovestory.lovestory.module.saveImageToLoveStoryFolderQPlus
import com.lovestory.lovestory.network.getNotSyncImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ImageSyncView: ViewModel()  {
    private val _downloadStatus = MutableLiveData<String>()
    val downloadStatus: LiveData<String>
        get() = _downloadStatus

    fun getImageFromServer(context: Context, token: String, photo_id: String) {
        Log.d("MODULE-getImageById", "호출됨")
        val localId = "lovestory-"+System.currentTimeMillis().toString()

        viewModelScope.launch(Dispatchers.IO) {
            Log.d("MODULE-getImageById", "파일 다운받기 시도")
            val response = getNotSyncImage(token, photo_id)
            if (response.isSuccessful) {
                Log.d("MODULE-getImageById", "파일 다운받기 성공")

                val responseBody = response.body()!!
                val imageFile = saveImageToLoveStoryFolderQPlus(context, responseBody, localId)
                Log.d("MODULE-getImageById", "파일 저장 완료 $imageFile")
                getImageInfoById(context, token, photo_id, imageFile.toString())
                Log.d("MODULE-getImageById", "DB 추가 완료 $imageFile")
                _downloadStatus.postValue("Image downloaded successfully")
            } else {
                _downloadStatus.postValue("Error downloading image")
            }
        }
    }
}