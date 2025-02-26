package com.example.playlistmaker.domain.model

import java.io.Serializable
import java.util.Date

data class Track(
    val trackId: Int,
    val trackName: String,
    val artistName: String,
    val trackTimeMillis: Int,
    val artworkUrl100: String,
    val collectionName: String,
    val releaseDate: Date?,
    val primaryGenreName: String,
    val country: String,
    val previewUrl: String?,
    var isFavorite: Boolean = false,
) : Serializable {
    fun getCoverArtwork() = artworkUrl100.replaceAfterLast('/', "512x512bb.jpg")
}