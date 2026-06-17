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

    @Query("SELECT * FROM drink_entries WHERE timestampMs >= :fromMs ORDER BY timestampMs DESC")
    suspend fun entriesFromSuspend(fromMs: Long): List<DrinkEntryEntity>

    @Query("SELECT * FROM drink_entries ORDER BY timestampMs DESC")
    suspend fun allEntries(): List<DrinkEntryEntity>

    @Insert
    suspend fun insert(entry: DrinkEntryEntity)

    @Query("UPDATE drink_entries SET amountMl = :amountMl, drinkTypeName = :drinkTypeName WHERE id = :id")
    suspend fun updateEntry(id: Long, amountMl: Int, drinkTypeName: String)

    @Query("DELETE FROM drink_entries WHERE id = :id")
    suspend fun deleteById(id: Long)
}

@Dao
interface SettingsDao {
    @Query("SELECT * FROM settings WHERE id = 1")
    fun observe(): Flow<SettingsEntity?>

    @Query("SELECT * FROM settings WHERE id = 1")
    suspend fun getSuspend(): SettingsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(settings: SettingsEntity)
}
