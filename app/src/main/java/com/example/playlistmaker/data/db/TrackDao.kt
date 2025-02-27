package com.example.playlistmaker.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackDao {

    @Insert(entity = TrackEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(tracks: TrackEntity)

    @Delete
    suspend fun deleteTrack(tracks: TrackEntity)

    @Query("SELECT * FROM favorite_tracks ORDER BY timestamp DESC")
    fun getTracks(): Flow<List<TrackEntity>>

    @Query("SELECT track_id FROM favorite_tracks")
    fun getTrackIds(): Flow<List<Int>>
}
