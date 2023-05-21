package com.lovestory.lovestory.database.entities

import androidx.lifecycle.LiveData
import androidx.room.*
import org.jetbrains.annotations.NotNull

@Entity(tableName = "additionalPhoto")
class AdditionalPhoto{
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
interface AdditionalPhotoDao{
    @Query("SELECT * FROM additionalPhoto")
    fun getAll():LiveData<List<AdditionalPhoto>>

    @Query("SELECT * FROM additionalPhoto WHERE photo_id = :id")
    fun getPhotoById(id : String): AdditionalPhoto?

    @Insert
    fun insertPhoto(photo: AdditionalPhoto)

    @Delete
    fun deletePhoto(photo: AdditionalPhoto)

    @Query("DELETE FROM additionalPhoto")
    fun deleteAll()
}