package com.example.playlistmaker.data.db.playlist.track_add_playlist

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PlaylistTrackDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTrack(track: PlaylistTrackEntity)

    @Query("SELECT * FROM playlist_track_entity WHERE trackId = :trackId")
    suspend fun getTrackById(trackId: Int): PlaylistTrackEntity?

    @Query("DELETE FROM playlist_tracks_cross WHERE playlistId = :playlistId AND trackId = :trackId")
    suspend fun removeTrackFromPlaylist(playlistId: Long, trackId: Long)

}
