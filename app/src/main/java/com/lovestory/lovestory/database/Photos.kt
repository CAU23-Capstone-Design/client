package com.lovestory.lovestory.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.lovestory.lovestory.database.entities.*

@Database(entities = [SyncedPhoto::class, PhotoForSync::class, AdditionalPhoto::class], version = 2)
abstract class PhotoDatabase : RoomDatabase(){
    abstract fun syncedPhotoDao(): SyncedPhotoDao
    abstract fun photoForSyncDao(): PhotoForSyncDao

    abstract fun additionalPhotoDao() : AdditionalPhotoDao

    companion object {
        @Volatile
        private var INSTANCE: PhotoDatabase? = null

        fun getDatabase(context: Context): PhotoDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PhotoDatabase::class.java,
                    "photo_database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}