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

@Database(
    entities = [DrinkEntryEntity::class, SettingsEntity::class],
    version = 3,
    exportSchema = false
)
abstract class WaterDatabase : RoomDatabase() {
    abstract fun drinkEntryDao(): DrinkEntryDao
    abstract fun settingsDao(): SettingsDao

    companion object {
        fun build(context: Context): WaterDatabase =
            Room.databaseBuilder(context, WaterDatabase::class.java, "water.db")
                .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                .build()
    }
}
