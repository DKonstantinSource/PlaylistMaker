package com.example.playlistmaker

import java.io.Serializable
import java.util.Date

data class SearchResponse (
     val resultCount: Int,
     val results: List<Track>
)

data class Track(
     val trackId: Int,
     val trackName: String,
     val artistName: String,
     val trackTimeMillis: Int,
     val artworkUrl100: String,
     val collectionName: String,
     val releaseDate: Date,
     val primaryGenreName: String,
     val country: String

) : Serializable {
     fun getCoverArtwork() = artworkUrl100.replaceAfterLast('/', "512x512bb.jpg")
}