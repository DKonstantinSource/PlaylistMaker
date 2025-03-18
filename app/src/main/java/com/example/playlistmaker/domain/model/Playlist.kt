package com.example.playlistmaker.domain.model

data class Playlist(
    val id: Long,
    val name: String,
    val description: String?,
    val imagePath: String?,
    val trackCount: Int,
    val tracks: List<Track>
)
