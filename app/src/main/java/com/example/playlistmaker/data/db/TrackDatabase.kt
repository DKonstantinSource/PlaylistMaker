package com.example.playlistmaker.data.db

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [TrackEntity::class], version = 1)
abstract class TrackDatabase : RoomDatabase() {
    abstract fun trackDao(): TrackDao

    companion object {
        @Volatile
        private var INSTANCE: TrackDatabase? = null

        fun getDatabase(context: Context): TrackDatabase {
            Log.d("TrackDatabase", "Creating database instance")
            return INSTANCE ?: synchronized(this) {
                Log.d("TrackDatabase", "Database synchronized")
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TrackDatabase::class.java,
                    "track_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }

    }
}
