package com.lovestory.lovestory.module

import android.content.Context
import android.util.Log
import com.lovestory.lovestory.database.PhotoDatabase
import com.lovestory.lovestory.database.entities.SyncedPhoto
import com.lovestory.lovestory.database.entities.SyncedPhotoDao
import com.lovestory.lovestory.database.repository.PhotoForSyncRepository
import com.lovestory.lovestory.database.repository.SyncedPhotoRepository
import com.lovestory.lovestory.model.PhotoInfo
import com.lovestory.lovestory.network.getPhotoTable
import com.lovestory.lovestory.view.ImageSyncView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

suspend fun checkExistNeedPhotoForSync(context : Context){
    lateinit var repository : SyncedPhotoRepository
    val token = getToken(context)

    CoroutineScope(Dispatchers.IO).launch {
        val response = getPhotoTable(token!!)
        if(response.isSuccessful){
            val photoDatabase = PhotoDatabase.getDatabase(context)
            val photoDao = photoDatabase.syncedPhotoDao()
            repository = SyncedPhotoRepository(photoDao)
            Log.d("MODULE-checkExistNeedPhotoForSync", "${response.body()}")
//            for(local_id in response.body()!!){
//                if(!repository.getPhotoForSyncById(local_id)){
//                    Log.d("MODULE-checkExistNeedPhotoForSync", "not existed $local_id")
////                    imageSyncView.getImageFromServer(context, token, local_id)
//                }
//            }
            val syncedPhotos = repository.listOfGetAllSyncedPhoto()

            val serverPhotosIdList = response.body()!!.map{it.local_id}

            deleteNotExistingIdsInServerList(syncedPhotos = syncedPhotos, repository=repository, serverIdList = serverPhotosIdList)

            val localPhotosIdList = syncedPhotos.map{it.id}

            syncLocalDatabaseWithServer(serverSyncedPhotos = response.body()!!, repository=repository, localPhotosIdList = localPhotosIdList)

        }
        else{
            Log.e("MODULE-checkExistNeedPhotoForSync", "${response.errorBody()}")
        }
    }
}

suspend fun deleteNotExistingIdsInServerList(syncedPhotos: List<SyncedPhoto>,repository: SyncedPhotoRepository ,serverIdList: List<String>) {

    val notExistingIdsInServer = syncedPhotos.filter { syncedPhoto -> syncedPhoto.id !in serverIdList }
    Log.d("FUNCTION-deleteNotExistingIdsInServerList","$notExistingIdsInServer")

    for (syncedPhoto in notExistingIdsInServer) {
        repository.deleteSyncedPhoto(syncedPhoto)
    }
}

suspend fun syncLocalDatabaseWithServer(serverSyncedPhotos : List<PhotoInfo>, repository: SyncedPhotoRepository, localPhotosIdList : List<String>) {

    val newSyncedPhotosFromServer = serverSyncedPhotos.filter { serverSyncedPhoto -> serverSyncedPhoto.local_id !in localPhotosIdList }
    Log.d("FUNCTION-syncLocalDatabaseWithServer","$newSyncedPhotosFromServer")

    val newLocalSyncedPhotos = newSyncedPhotosFromServer.map { serverSyncedPhoto ->
        SyncedPhoto(
            id = serverSyncedPhoto.local_id,
            date = serverSyncedPhoto.date,
            area1 = serverSyncedPhoto.location.area1,
            area2 = serverSyncedPhoto.location.area2,
            area3 = serverSyncedPhoto.location.area3,
            latitude = serverSyncedPhoto.latitude,
            longitude = serverSyncedPhoto.longitude
        )
    }

    for (item in newLocalSyncedPhotos){
        repository.insertSyncedPhoto(item)
    }
//    repository.insertSyncedPhotosByList(newLocalSyncedPhotos)

}