package com.example.playlistmaker.domain.model

data class Playlist(
    val id: Long,
    val name: String,
    val description: String?,
    val imagePath: String?,
    val tracks: List<Track>,
    val trackCount: Int
)
