package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.model.Track

interface ManageSearchHistory {
    fun getSearchHistory(): List<Track>
    fun addToHistory(track: Track)
    fun clearHistory()
}