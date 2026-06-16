package dev.yichen.watertracker.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [DrinkEntryEntity::class, SettingsEntity::class],
    version = 1,
    exportSchema = false
)
abstract class WaterDatabase : RoomDatabase() {
    abstract fun drinkEntryDao(): DrinkEntryDao
    abstract fun settingsDao(): SettingsDao

    companion object {
        fun build(context: Context): WaterDatabase =
            Room.databaseBuilder(context, WaterDatabase::class.java, "water.db").build()
    }
}
