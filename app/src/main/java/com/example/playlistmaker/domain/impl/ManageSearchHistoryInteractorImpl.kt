package com.example.playlistmaker.domain.impl


import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.domain.repository.SearchHistoryRepository
import com.example.playlistmaker.domain.use_case.ManageSearchHistoryUseCase

class ManageSearchHistoryInteractorImpl(private val repository: SearchHistoryRepository) :
    ManageSearchHistoryUseCase {
    override fun getSearchHistory(): List<Track> {
        return repository.getSearchHistory()
    }

    override fun addToHistory(track: Track) {
        repository.addToHistory(track)
    }

    override fun clearHistory() {
        repository.clearHistory()
    }
}