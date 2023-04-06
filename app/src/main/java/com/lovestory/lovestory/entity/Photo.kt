package com.lovestory.lovestory.entity

import androidx.lifecycle.LiveData
import androidx.room.*
import org.jetbrains.annotations.NotNull

@Entity(tableName = "photos")
class Photo {
    @PrimaryKey
    @NotNull
    @ColumnInfo(name = "photo_id")
    var id: String = ""

    @ColumnInfo(name = "date")
    @NotNull
    var date: String = ""

    @ColumnInfo(name = "isSynced")
    @NotNull
    var isSynced: Boolean = false

    @ColumnInfo(name = "location")
    var location: String? = null


    var imageUrl: String? = null
    var latitude: Double = 0.0
    var longitude: Double = 0.0

    constructor() {}
    constructor(id: String, date : String, imageUrl : String,  latitude : Double, longitude : Double){
        this.id = id
        this.date = date
        this.imageUrl = imageUrl
        this.latitude = latitude
        this.longitude = longitude
    }
}

@Dao
interface PhotoDao {
    @Query("SELECT * FROM photos")
    fun getAllPhotos(): LiveData<List<Photo>>

    @Query("SELECT * FROM photos WHERE photo_id = :id")
    suspend fun getPhotoById(id: Int): Photo?

    @Query("SELECT * FROM photos WHERE date = :requestDate")
    fun getRequestDatePhotos(requestDate : String) : LiveData<List<Photo>>

    @Insert
    fun insertPhoto(photo: Photo)
}