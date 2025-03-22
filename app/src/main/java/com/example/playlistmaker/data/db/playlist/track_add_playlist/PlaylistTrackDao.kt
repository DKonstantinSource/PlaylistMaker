package com.example.playlistmaker.data.db.playlist.track_add_playlist

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.data.model.Track

@Dao
interface PlaylistTrackDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTrack(track: PlaylistTrackEntity)

    @Query("SELECT * FROM playlist_track_entity WHERE track_id = :trackId")
    suspend fun getTrackById(trackId: Long): PlaylistTrackEntity?

    @Query("DELETE FROM playlist_tracks_cross WHERE playlistId = :playlistId AND trackId = :trackId")
    suspend fun removeTrackFromPlaylist(playlistId: Long, trackId: Long)

    @Query(
        """
    SELECT * FROM playlist_track_entity 
    WHERE track_id IN (SELECT trackId FROM playlist_tracks_cross WHERE playlistId = :playlistId)
"""
    )
    suspend fun getTracksByEnterPlaylist(playlistId: Long): List<PlaylistTrackEntity>

}
