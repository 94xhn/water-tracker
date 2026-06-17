package dev.yichen.watertracker.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CustomDrinkDao {
    @Query("SELECT * FROM custom_drinks ORDER BY id ASC")
    fun all(): Flow<List<CustomDrinkEntity>>

    @Query("SELECT * FROM custom_drinks ORDER BY id ASC")
    suspend fun allSuspend(): List<CustomDrinkEntity>

    @Insert
    suspend fun insert(entity: CustomDrinkEntity): Long

    @Query("DELETE FROM custom_drinks WHERE id = :id")
    suspend fun deleteById(id: Long)
}
