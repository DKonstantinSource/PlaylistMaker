package com.example.playlistmaker.data.network

import com.example.playlistmaker.data.model.Track


data class SearchResponse(
    val resultCount: Int,
    val results: List<Track>
)