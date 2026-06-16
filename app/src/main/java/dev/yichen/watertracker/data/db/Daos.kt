package dev.yichen.watertracker.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DrinkEntryDao {
    @Query("SELECT * FROM drink_entries WHERE timestampMs >= :startMs ORDER BY timestampMs DESC")
    fun entriesFrom(startMs: Long): Flow<List<DrinkEntryEntity>>

    @Insert
    suspend fun insert(entry: DrinkEntryEntity)

    @Query("DELETE FROM drink_entries WHERE id = :id")
    suspend fun deleteById(id: Long)
}

@Dao
interface SettingsDao {
    @Query("SELECT * FROM settings WHERE id = 1")
    fun observe(): Flow<SettingsEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(settings: SettingsEntity)
}
