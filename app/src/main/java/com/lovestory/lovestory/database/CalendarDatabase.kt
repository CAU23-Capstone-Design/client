package com.lovestory.lovestory.database

import androidx.room.*

@Entity(tableName = "string_memory")
data class StringMemoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: String,
    var comment: String
)

@Dao
interface StringMemoryDao {
    @Query("SELECT * FROM string_memory")
    fun getAll(): List<StringMemoryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(stringMemoryEntity: StringMemoryEntity)
}

@Database(entities = [StringMemoryEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun stringMemoryDao(): StringMemoryDao
}