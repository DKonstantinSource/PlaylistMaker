package com.example.playlistmaker.domain.repository

import com.example.playlistmaker.domain.model.Track

interface TrackAddToPlaylistRepository {
    suspend fun addTrackToPlaylist(track: Track, playlistId: Long)
    suspend fun removeTrackFromPlaylist(playlistId: Long, trackId: Long)
    suspend fun isTrackInPlaylist(playlistId: Long, trackId: Long): Boolean
    suspend fun getCurrentlyCountTrack(playlistId: Long): Int
}
