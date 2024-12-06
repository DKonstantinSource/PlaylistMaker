package com.example.playlistmaker.domain.use_case

import com.example.playlistmaker.domain.model.Track

interface ManageSearchHistoryUseCase {
    fun getSearchHistory(): List<Track>
    fun addToHistory(track: Track)
    fun clearHistory()
}