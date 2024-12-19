package com.example.playlistmaker.data.network.response

import com.example.playlistmaker.data.model.Track


data class SearchResponse(
    val resultCount: Int,
    val results: List<Track>
)