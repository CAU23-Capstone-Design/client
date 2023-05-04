package com.lovestory.lovestory.database.entities

import androidx.lifecycle.LiveData
import androidx.room.*
import org.jetbrains.annotations.NotNull

@Entity(tableName = "syncedPhotos")
class SyncedPhoto{
    @PrimaryKey
    @NotNull
    @ColumnInfo(name = "photo_id")
    var id: String = ""

    @ColumnInfo(name = "date")
    var date: String = ""

    @ColumnInfo(name = "area1")
    var area1 : String = ""

    @ColumnInfo(name = "area2")
    var area2 : String = ""

    @ColumnInfo(name = "area3")
    var area3 : String = ""

    var latitude: Double = 0.0
    var longitude: Double = 0.0

    constructor(id: String, date : String, area1 : String, area2 : String, area3 : String,latitude : Double, longitude : Double){
        this.id = id
        this.date = date
        this.area1 = area1
        this.area2 = area2
        this.area3 = area3
        this.latitude = latitude
        this.longitude = longitude
    }
}

@Dao
interface SyncedPhotoDao {
    @Query("SELECT * FROM syncedPhotos")
    fun getAll(): LiveData<List<SyncedPhoto>>

    @Query("SELECT * FROM syncedPhotos")
    fun listOfGetAll(): List<SyncedPhoto>

    @Insert
    fun insert(syncedPhoto: SyncedPhoto)

    @Insert
    fun insertAllSyncedPhotos(syncedPhotos: List<SyncedPhoto>)

    @Delete
    fun delete(syncedPhoto: SyncedPhoto)

    @Query("SELECT * FROM syncedPhotos ORDER BY date DESC")
    fun getAllSyncedPhotosSortedByDate(): LiveData<List<SyncedPhoto>>

    @Query("SELECT * FROM syncedPhotos WHERE date IN (SELECT MIN(date) FROM syncedPhotos GROUP BY substr(date, 1, 10)) ORDER BY date DESC")
    fun getFirstPhotoForEachDay(): LiveData<List<SyncedPhoto>>

    @Query("SELECT * FROM syncedPhotos WHERE date = :targetDate")
    suspend fun getPhotosByDate(targetDate: String): List<SyncedPhoto>

    @Query("SELECT * FROM syncedPhotos WHERE area2 = :targetArea2")
    suspend fun getPhotosByArea2(targetArea2: String): List<SyncedPhoto>

    @Query("SELECT * FROM syncedPhotos WHERE date = :targetDate ORDER BY photo_id ASC LIMIT 1")
    suspend fun getFirstPhotoByDate(targetDate: String): SyncedPhoto?

    @Query("SELECT * FROM syncedPhotos WHERE photo_id = :id")
    suspend fun getPhotoById(id: String): PhotoForSync?

}