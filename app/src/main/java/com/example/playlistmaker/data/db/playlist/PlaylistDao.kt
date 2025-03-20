package com.example.playlistmaker.data.db.playlist

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.playlistmaker.data.db.playlist.track_add_playlist.PlaylistTrackCrossRef


@Dao
interface PlaylistDao {
    @Insert
    suspend fun insertPlaylist(playlist: PlaylistEntity)

    @Update
    suspend fun updatePlaylist(playlist: PlaylistEntity)

    @Query("SELECT * FROM playlist_table WHERE playlistId = :id")
    suspend fun getPlaylistById(id: Long): PlaylistEntity?

    @Query("SELECT * FROM playlist_table")
    suspend fun getAllPlaylists(): List<PlaylistEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addTrackToPlaylist(crossRef: PlaylistTrackCrossRef)

    @Query("DELETE FROM playlist_tracks_cross WHERE playlistId = :playlistId AND trackId = :trackId")
    suspend fun removeTrackFromPlaylist(playlistId: Long, trackId: Long)

    @Query("DELETE FROM playlist_table")
    suspend fun clearPlaylists()

    @Query("SELECT trackId FROM playlist_tracks_cross WHERE playlistId = :playlistId")
    suspend fun getTrackIdsForPlaylist(playlistId: Long): List<Long>

    @Query("DELETE FROM playlist_tracks_cross WHERE playlistId = :playlistId")
    suspend fun clearTracksFromPlaylist(playlistId: Long)

    @Query("UPDATE playlist_table SET tracks = :updatedTracks, trackCount = trackCount + 1 WHERE playlistId = :playlistId")
    suspend fun updatePlaylistTracks(playlistId: Long, updatedTracks: String)

    @Query("UPDATE playlist_table SET trackCount = :count WHERE playlistId = :playlistId")
    suspend fun updateTrackCount(playlistId: Long, count: Int)



}
