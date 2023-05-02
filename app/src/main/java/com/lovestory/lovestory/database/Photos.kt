package com.lovestory.lovestory.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.lovestory.lovestory.database.entities.PhotoForSync
import com.lovestory.lovestory.database.entities.PhotoForSyncDao
import com.lovestory.lovestory.database.entities.SyncedPhoto
import com.lovestory.lovestory.database.entities.SyncedPhotoDao

@Database(entities = [SyncedPhoto::class, PhotoForSync::class], version = 1)
abstract class PhotoDatabase : RoomDatabase(){
    abstract fun syncedPhotoDao(): SyncedPhotoDao
    abstract fun photoForSyncDao(): PhotoForSyncDao

    companion object {
        @Volatile
        private var INSTANCE: PhotoDatabase? = null

        fun getDatabase(context: Context): PhotoDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PhotoDatabase::class.java,
                    "photo_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}