package com.example.playlistmaker.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.playlistmaker.data.db.playlist.PlaylistDao
import com.example.playlistmaker.data.db.playlist.PlaylistEntity
import com.example.playlistmaker.data.db.playlist.PlaylistTrackCrossRef
import com.example.playlistmaker.data.db.playlist.track.PlaylistTrackDao
import com.example.playlistmaker.data.db.playlist.track.PlaylistTrackEntity
import com.example.playlistmaker.data.db.track.TrackDao
import com.example.playlistmaker.data.db.track.TrackEntity


@Database(
    entities = [
        PlaylistEntity::class,
        TrackEntity::class,
        PlaylistTrackCrossRef::class,
        PlaylistTrackEntity::class],
    version = 6,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun playlistDao(): PlaylistDao
    abstract fun trackDao(): TrackDao
    abstract fun playlistTrackDao(): PlaylistTrackDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS playlist_table (
                        playlistId INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 
                        name TEXT NOT NULL, 
                        description TEXT, 
                        imagePath TEXT, 
                        trackCount INTEGER NOT NULL DEFAULT 0, 
                        tracks TEXT NOT NULL, 
                        createdAt INTEGER NOT NULL DEFAULT 0
                    )
                    """.trimIndent()
                )

                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS playlist_track_cross_ref (
                        playlistId INTEGER NOT NULL, 
                        trackId INTEGER NOT NULL, 
                        PRIMARY KEY (playlistId, trackId), 
                        FOREIGN KEY (playlistId) REFERENCES playlist_table (playlistId) ON DELETE CASCADE, 
                        FOREIGN KEY (trackId) REFERENCES track_table (trackId) ON DELETE CASCADE
                    )
                    """.trimIndent()
                )

                database.execSQL("ALTER TABLE favorite_track_table RENAME TO track_table")
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                    .addMigrations(MIGRATION_5_6)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

