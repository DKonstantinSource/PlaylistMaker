package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.TrackAddToPlaylistInteractor
import com.example.playlistmaker.domain.repository.TrackAddToPlaylistRepository
import com.example.playlistmaker.domain.model.Track

class TrackAddToPlaylistInteractorImpl(
    private val trackAddToPlaylistRepository: TrackAddToPlaylistRepository
) : TrackAddToPlaylistInteractor {

    override suspend fun addTrackToPlaylist(track: Track, playlistId: Long) {
        trackAddToPlaylistRepository.addTrackToPlaylist(track, playlistId)
    }

    override suspend fun removeTrackFromPlaylist(playlistId: Long, trackId: Long) {
        trackAddToPlaylistRepository.removeTrackFromPlaylist(playlistId, trackId)
    }

    override suspend fun isTrackInPlaylist(playlistId: Long, trackId: Long): Boolean {
        return trackAddToPlaylistRepository.isTrackInPlaylist(playlistId, trackId)
    }

    override suspend fun getCurrentlyCountTrack(playlistId: Long): Int {
        return trackAddToPlaylistRepository.getCurrentlyCountTrack(playlistId)
    }

    override suspend fun getTracksForPlaylist(playlistId: Long): List<Track> {
        return trackAddToPlaylistRepository.getTracksForPlaylist(playlistId)
    }

}
