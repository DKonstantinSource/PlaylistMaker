package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.PlaylistInteractor
import com.example.playlistmaker.domain.model.Playlist
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.domain.repository.PlayListRepository

class PlaylistInteractorImpl(private val playlistRepository: PlayListRepository) :
    PlaylistInteractor {

    override suspend fun createPlaylist(playlist: Playlist) {
        playlistRepository.createPlaylist(playlist)
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        playlistRepository.updatePlaylist(playlist)
    }

    override suspend fun getPlaylistById(id: Long): Playlist? {
        return playlistRepository.getPlaylistById(id)
    }

    override suspend fun getAllPlaylists(): List<Playlist> {
        return playlistRepository.getAllPlaylists()
    }
    override suspend fun addTrackToPlaylist(track: Track, playlist: Playlist) {
        playlistRepository.addTrackToPlaylist(track, playlist)
    }
}
