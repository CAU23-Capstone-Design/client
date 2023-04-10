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

    @ColumnInfo(name = "area1")
    var area1: String? = null

    @ColumnInfo(name = "area2")
    var area2: String? = null

    @ColumnInfo(name = "area3")
    var area3 : String? = null

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
    constructor(
        id: String,
        date : String,
        imageUrl : String,
        latitude : Double,
        longitude : Double,
        isSynced : Boolean,
        area1: String,
        area2: String,
        area3: String
    ){
        this.id = id
        this.date = date
        this.imageUrl = imageUrl
        this.latitude = latitude
        this.longitude = longitude
        this.isSynced = true
        this.area1 = area1
        this.area2 = area2
        this.area3 = area3
    }
}

@Dao
interface PhotoDao {
    @Query("SELECT * FROM photos")
    fun getAllPhotos(): LiveData<List<Photo>>

    @Query("SELECT * FROM photos WHERE photo_id = :id")
    suspend fun getPhotoById(id: String): Photo?

    @Query("SELECT * FROM photos WHERE date = :requestDate")
    fun getRequestDatePhotos(requestDate : String) : LiveData<List<Photo>?>

    @Query("SELECT * FROM photos WHERE isSynced = 0")
    fun getNotSyncedPhotos() : LiveData<List<Photo>>

    @Query("SELECT * FROM photos WHERE isSynced = 1")
    fun getSyncedPhotos() : LiveData<List<Photo>>

    @Insert
    fun insertPhoto(photo: Photo)

    @Update
    fun updatePhoto(photo: Photo)

    @Delete
    fun deletePhoto(photo: Photo)

    @Query("UPDATE photos SET isSynced = 1, area1 = :area1, area2 = :area2, area3 = :area3 WHERE photo_id = :id")
    suspend fun updatePhotoSyncStatusAndLocationById(id: String, area1: String, area2: String, area3: String)
//    @Query("UPDATE photos SET isSynced = 1 WHERE photo_id = :id")
//    suspend fun setPhotoSyncedById(id: String)

}