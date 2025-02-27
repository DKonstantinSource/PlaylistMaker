package com.example.playlistmaker.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.playlistmaker.Converters

@Database(entities = [TrackEntity::class], version = 4, exportSchema = false)
@TypeConverters(Converters::class)
abstract class TrackDatabase : RoomDatabase() {
    abstract fun trackDao(): TrackDao

    companion object {
        private const val DATABASE_NAME = "track_database"

        val MIGRATION_1_2 = object : Migration(4, 5) {
            override fun migrate(database: SupportSQLiteDatabase) {

                database.execSQL("ALTER TABLE favorite_track_table ADD COLUMN new_column_name TEXT")
            }
        }

        fun create(context: Context): TrackDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                TrackDatabase::class.java,
                DATABASE_NAME
            )
                .addMigrations(MIGRATION_1_2)
                .fallbackToDestructiveMigration()
                .build()

        }
    }
}
