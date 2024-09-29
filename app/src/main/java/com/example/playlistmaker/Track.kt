package com.example.playlistmaker
data class SearchResponse (
     val resultCount: Int,
     val results: List<Track>
)

data class Track(
     val trackName: String,
     val artistName: String,
     val trackTimeMillis: String,
     val artworkUrl100: String
)