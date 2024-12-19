package com.example.playlistmaker.presentation.view_model.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.domain.api.ManageSearchHistory
import com.example.playlistmaker.domain.impl.SearchTracksInteractorImpl


class SearchViewModelFactory(
    private val manageSearchHistory: ManageSearchHistory,
    private val searchTracksInteractorImpl: SearchTracksInteractorImpl
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {
            return SearchViewModel(manageSearchHistory, searchTracksInteractorImpl) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}