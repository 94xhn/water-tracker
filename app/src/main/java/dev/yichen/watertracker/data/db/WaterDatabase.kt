package dev.yichen.watertracker.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

private val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            "ALTER TABLE settings ADD COLUMN cupSizesJson TEXT NOT NULL DEFAULT '150,200,250,300'"
        )
    }
}

private val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            "ALTER TABLE drink_entries ADD COLUMN drinkTypeName TEXT NOT NULL DEFAULT 'WATER'"
        )
    }
}

private val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """CREATE TABLE IF NOT EXISTS custom_drinks (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                name TEXT NOT NULL,
                emoji TEXT NOT NULL DEFAULT '💧',
                hydrationFactor REAL NOT NULL DEFAULT 1.0
            )"""
        )
    }
}

@Database(
    entities = [DrinkEntryEntity::class, SettingsEntity::class, CustomDrinkEntity::class],
    version = 4,
    exportSchema = false
)
abstract class WaterDatabase : RoomDatabase() {
    abstract fun drinkEntryDao(): DrinkEntryDao
    abstract fun settingsDao(): SettingsDao
    abstract fun customDrinkDao(): CustomDrinkDao

    companion object {
        fun build(context: Context): WaterDatabase =
            Room.databaseBuilder(context, WaterDatabase::class.java, "water.db")
                .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
                .build()
    }
}
