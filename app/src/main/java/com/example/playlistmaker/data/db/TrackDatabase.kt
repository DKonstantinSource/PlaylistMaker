package com.example.playlistmaker.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [TrackEntity::class], version = 1, exportSchema = false)
abstract class TrackDatabase : RoomDatabase() {
    abstract fun trackDao(): TrackDao

    companion object {
        fun create(context: Context): TrackDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                TrackDatabase::class.java,
                "track_database"
            )
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}
