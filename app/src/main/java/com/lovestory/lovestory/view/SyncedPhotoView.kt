package com.lovestory.lovestory.view

import android.app.Application
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.lovestory.lovestory.database.PhotoDatabase
import com.lovestory.lovestory.database.entities.SyncedPhoto
import com.lovestory.lovestory.database.repository.SyncedPhotoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SyncedPhotoView(application:Application): ViewModel() {
    private lateinit var syncedPhotoRepository: SyncedPhotoRepository

    lateinit var listOfSyncPhotos : LiveData<List<SyncedPhoto>>
    lateinit var dayListOfSyncedPhotos :LiveData<List<SyncedPhoto>>
    lateinit var monthListOfSyncedPhotos : LiveData<List<SyncedPhoto>>
    lateinit var yearListOfSyncedPhotos : LiveData<List<SyncedPhoto>>

    lateinit var groupedSyncedPhotosByDate: LiveData<Map<String, List<SyncedPhoto>>>
    lateinit var syncedPhotosByDateAndArea : LiveData<Map<String, Map<Pair<String, String>, List<SyncedPhoto>>>>
    lateinit var sizesOfInnerElements : LiveData<List<List<Int>>>
    lateinit var cumOfSizeOfInnerElements : LiveData<List<List<Int>>>

    lateinit var daySyncedPhotosByDate : LiveData<Map<String, List<SyncedPhoto>>>
    lateinit var daySyncedPhotosByMonth : LiveData<Map<String, Map<String, List<SyncedPhoto>>>>
    lateinit var sizeOfDaySyncedPhotos : LiveData<List<List<Int>>>
    lateinit var cumOfDaySyncedPhotos : LiveData<List<List<Int>>>

    lateinit var monthSyncedPhotosByYear : LiveData<Map<String, Map<String, List<SyncedPhoto>>>>
    lateinit var sizeOfMonthSyncedPhotos : LiveData<List<List<Int>>>
    lateinit var cumOfMonthSyncedPhotos : LiveData<List<List<Int>>>

    private val _syncedPhoto = MutableLiveData<SyncedPhoto?>()
    val syncedPhoto: LiveData<SyncedPhoto?> = _syncedPhoto

    var selectedPhotosSet = mutableStateOf( mutableSetOf<String>())

    init {
        val photoDatabase = PhotoDatabase.getDatabase(application)
        val syncedPhotoDao =  photoDatabase.syncedPhotoDao()
        syncedPhotoRepository = SyncedPhotoRepository(syncedPhotoDao)

        listOfSyncPhotos = syncedPhotoRepository.getAllSyncedPhotos
        dayListOfSyncedPhotos = syncedPhotoRepository.getDaySyncedPhotos
        monthListOfSyncedPhotos = syncedPhotoRepository.getMonthSyncedPhotos
        yearListOfSyncedPhotos = syncedPhotoRepository.getYearSyncedPhotos

        groupedSyncedPhotosByDate = Transformations.map(listOfSyncPhotos) { syncedPhotos ->
            syncedPhotos.groupBy { it.date.substring(0, 10) }
        }

        syncedPhotosByDateAndArea = Transformations.map(listOfSyncPhotos){syncedPhotos->
            syncedPhotos.groupBy { it.date.substring(0, 10) }
                .mapValues { entry ->
                    entry.value.groupBy { Pair(it.area1, it.area2) } }
        }

        sizesOfInnerElements = Transformations.map(syncedPhotosByDateAndArea){syncedPhotosByDateAndAreaMap->
            syncedPhotosByDateAndAreaMap.map { entry ->
                entry.value.map { groupedByArea ->
                    groupedByArea.value.size
                }
            }
        }
        cumOfSizeOfInnerElements = Transformations.map(sizesOfInnerElements){
            computeCumulativeSizes(it)
        }

        daySyncedPhotosByDate = Transformations.map(dayListOfSyncedPhotos){syncedPhotos->
            syncedPhotos.groupBy { it.date.substring(0, 10) }
        }
        daySyncedPhotosByMonth = Transformations.map(dayListOfSyncedPhotos){syncedPhotos->
            syncedPhotos.groupBy { it.date.substring(0, 7) }.mapValues {entry->
                entry.value.groupBy { it.date.substring(0, 10) }
            }
        }
        sizeOfDaySyncedPhotos = Transformations.map(daySyncedPhotosByMonth){daySyncedPhotosByMonth->
            daySyncedPhotosByMonth.map { entry ->
                entry.value.map { groupedByMonth ->
                    groupedByMonth.value.size
                }
            }
        }
        cumOfDaySyncedPhotos = Transformations.map(sizeOfDaySyncedPhotos){
            computeCumulativeSizesForMonth(it)
        }

        monthSyncedPhotosByYear = Transformations.map(monthListOfSyncedPhotos){syncedPhotos->
            syncedPhotos.groupBy { it.date.substring(0, 4) }.mapValues {entry->
                entry.value.groupBy { it.date.substring(0, 7) }
            }
        }

        sizeOfMonthSyncedPhotos = Transformations.map(monthSyncedPhotosByYear){monthSyncedPhotosByYear->
            monthSyncedPhotosByYear.map { entry ->
                entry.value.map { groupedByMonth ->
                    groupedByMonth.value.size
                }
            }
        }

        cumOfMonthSyncedPhotos = Transformations.map(sizeOfMonthSyncedPhotos){
            computeCumulativeSizesForMonth(it)
        }

    }

    fun computeCumulativeSizes(sizes: List<List<Int>>): List<List<Int>> {
        val cumulativeList = mutableListOf<MutableList<Int>>()

        var cumValue = 0
        for (i in sizes.indices) {
            cumValue+=1
            cumulativeList.add(mutableListOf())
            for (j in 0 until sizes[i].size) {
                if(sizes[i][j]%3 != 0){cumValue += 1}
                cumValue += sizes[i][j]/3
                cumulativeList[i].add(cumValue)
            }
        }
        return cumulativeList
    }

    fun computeCumulativeSizesForMonth(sizes: List<List<Int>>): List<List<Int>> {
        val cumulativeList = mutableListOf<MutableList<Int>>()

        var cumValue = 0
        for (i in sizes.indices) {
            cumulativeList.add(mutableListOf())
            for (j in 0 until sizes[i].size) {
                cumValue += sizes[i][j]+1
                cumulativeList[i].add(cumValue)
            }
        }
        return cumulativeList
    }

    private fun groupByYearMonth(data: Map<String, List<SyncedPhoto>>): Map<String, List<SyncedPhoto>> {
        val result = mutableMapOf<String, MutableList<SyncedPhoto>>()

        data.forEach { (date, photos) ->
            val yearMonth = date.substring(0, 7)
            val list = result.getOrPut(yearMonth) { mutableListOf() }

            list.addAll(photos)
        }

        return result
    }

    fun updateSyncedPhoto(newPhoto: SyncedPhoto) {
        _syncedPhoto.value = newPhoto
    }

    fun getAllSyncedPhotoIndex(photo: SyncedPhoto):Int{
        return listOfSyncPhotos.value!!.indexOf(photo)
    }

    fun calendarSyncedPhotoIndex(photo: SyncedPhoto, date: String):Int{
        val filteredList = listOfSyncPhotos.value?.filter { it.date.substring(0,10) == date }
        return filteredList!!.indexOf(photo)
    }

    fun addSelectedPhotosSet(id : String){
        selectedPhotosSet.value.add(id)
    }

    fun removeSelectedPhotosSet(id : String){
        selectedPhotosSet.value.remove(id)
    }

    fun isExistSelectedPhotosSetById(id :String):Boolean{
        return selectedPhotosSet.value.contains(id)
    }

    fun clearSelectedPhotosSet(){
        selectedPhotosSet.value.clear()
    }
}