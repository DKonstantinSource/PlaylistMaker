package com.example.playlistmaker.domain.repository

import com.example.playlistmaker.domain.model.Playlist

interface PlayListRepository {

    suspend fun createPlaylist(playlist: Playlist)

    suspend fun updatePlaylist(playlist: Playlist)

    suspend fun getPlaylistById(id: Long): Playlist?

    suspend fun getAllPlaylists(): List<Playlist>
}
