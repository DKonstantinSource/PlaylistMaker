package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.model.Playlist

interface PlaylistInteractor {

    suspend fun createPlaylist(playlist: Playlist)

    suspend fun updatePlaylist(playlist: Playlist)

    suspend fun getPlaylistById(id: Long): Playlist?

    suspend fun getAllPlaylists(): List<Playlist>
}
