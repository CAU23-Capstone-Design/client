package com.lovestory.lovestory.database.entities

import androidx.lifecycle.LiveData
import androidx.room.*
import org.jetbrains.annotations.NotNull

@Entity(tableName = "photoForSync")
class PhotoForSync {
    @PrimaryKey
    @NotNull
    @ColumnInfo(name = "photo_id")
    var id: String = ""

    @ColumnInfo(name = "date")
    @NotNull
    var date: String = ""

    var imageUrl: String? = null
    var latitude: Double = 0.0
    var longitude: Double = 0.0

    constructor(id: String, date : String, imageUrl : String, latitude : Double, longitude : Double){
        this.id = id
        this.date = date
        this.imageUrl = imageUrl
        this.latitude = latitude
        this.longitude = longitude
    }
}

@Dao
interface PhotoForSyncDao {
    @Query("SELECT * FROM photoForSync")
    fun getAll(): LiveData<List<PhotoForSync>>

    @Query("SELECT * FROM photoForSync WHERE photo_id = :id")
    suspend fun getPhotoById(id: String): PhotoForSync?

    @Insert
    fun insertPhoto(photo: PhotoForSync)

    @Delete
    fun deletePhoto(photo: PhotoForSync)
}