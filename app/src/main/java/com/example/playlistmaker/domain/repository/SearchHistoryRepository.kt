package com.example.playlistmaker.domain.repository

import com.example.playlistmaker.domain.model.Track

interface SearchHistoryRepository {
    fun getSearchHistory(): List<Track>
    fun addToHistory(track: Track)
    fun clearHistory()
}