package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.model.Track

interface TrackAddToPlaylistInteractor {
    suspend fun addTrackToPlaylist(track: Track, playlistId: Long)
    suspend fun removeTrackFromPlaylist(playlistId: Long, trackId: Long)
    suspend fun isTrackInPlaylist(playlistId: Long, trackId: Long): Boolean
    suspend fun getCurrentlyCountTrack(playlistId: Long): Int
    suspend fun getTracksForPlaylist(playlistId: Long): List<Track>
}
