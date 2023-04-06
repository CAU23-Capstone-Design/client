package com.lovestory.lovestory.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.lovestory.lovestory.entity.Photo
import com.lovestory.lovestory.entity.PhotoDao

@Database(entities = [(Photo::class)], version = 1)
abstract class PhotoDatabase : RoomDatabase(){
    abstract fun photoDao() : PhotoDao

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