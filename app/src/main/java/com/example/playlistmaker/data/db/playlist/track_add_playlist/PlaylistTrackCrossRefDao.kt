package com.example.playlistmaker.data.db.playlist.track_add_playlist

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query


@Dao
interface PlaylistTrackCrossRefDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCrossRef(crossRef: PlaylistTrackCrossRef)

    @Query("DELETE FROM playlist_tracks_cross WHERE playlistId = :playlistId AND trackId = :trackId")
    suspend fun deleteTrackFromPlaylist(playlistId: Long, trackId: Long)

    @Query("SELECT COUNT(*) FROM playlist_tracks_cross WHERE playlistId = :playlistId AND trackId = :trackId")
    suspend fun isTrackInPlaylist(playlistId: Long, trackId: Long): Boolean
}