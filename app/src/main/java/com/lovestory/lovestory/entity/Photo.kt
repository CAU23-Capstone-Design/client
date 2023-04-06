package com.lovestory.lovestory.entity

import androidx.lifecycle.LiveData
import androidx.room.*
import org.jetbrains.annotations.NotNull

@Entity(tableName = "photos")
class Photo {
    @PrimaryKey(autoGenerate = true)
    @NotNull
    @ColumnInfo(name = "photo_id")
    var id: Int = 0

    @ColumnInfo(name = "date")
    @NotNull
    var date: String = ""

    @ColumnInfo(name = "isSynced")
    @NotNull
    var isSynced: Boolean = false

    @ColumnInfo(name = "location")
    private var location: String? = null


    private var imageUrl: String? = null
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0

    constructor() {}
    constructor(date : String, imageUrl : String,  latitude : Double, longitude : Double){
        this.date = date
        this.imageUrl = imageUrl
        this.latitude = latitude
        this.longitude = longitude
    }
}

@Dao
interface PhotoDao {
    @Query("SELECT * FROM photos")
    fun getPhotos(): LiveData<List<Photo>>

    @Query("SELECT * FROM photos WHERE photo_id = :id")
    suspend fun getPhotoById(id: Int): Photo?

    @Query("SELECT * FROM photos WHERE date =: requestDate")
    fun getRequestDatePhotos(requestDate : String) : LiveData<List<Photo>>

    @Insert
    fun insertPhoto(photo: Photo)
}