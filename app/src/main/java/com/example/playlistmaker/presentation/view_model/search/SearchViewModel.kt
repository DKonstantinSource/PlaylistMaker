package com.example.playlistmaker.presentation.view_model.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.Constants.SEARCH_DEBOUNCE_DELAY
import com.example.playlistmaker.domain.api.ManageSearchHistory
import com.example.playlistmaker.domain.impl.SearchTracksInteractorImpl
import com.example.playlistmaker.domain.model.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchViewModel(
    private val manageSearchHistory: ManageSearchHistory,
    private var searchTracksInteractorImpl: SearchTracksInteractorImpl
) : ViewModel() {

    private val _tracks = MutableLiveData<List<Track>>()
    val tracks: LiveData<List<Track>> get() = _tracks

    private val _selectedTrack = MutableLiveData<Track?>()
    val selectedTrack: LiveData<Track?> get() = _selectedTrack

    private var searchJob: Job? = null
    private var searchQuery: String = ""
    private var storyState = true
    private var checkStateAfterSearch = false

    init {
        getHistoryTrack()
    }


    fun trackClicked(track: Track) {
        manageSearchHistory.addToHistory(track)
        _selectedTrack.value = track

        _tracks.value = _tracks.value?.map {
            if (it.trackId == track.trackId) {
                it.copy(isFavorite = !it.isFavorite)
            } else {
                it
            }
        }
    }

    fun updateTracks() {
        if (getQuery().isNotEmpty()) {
            searchTracks(getQuery())
        } else {
            getHistoryTrack()
        }
    }

    fun onSearchQueryChanged(query: String) {
        searchQuery = query
        if (searchQuery.isBlank()) {
            getHistoryTrack()
        } else {
            searchDebounce()
        }
    }

    private fun searchDebounce() {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(SEARCH_DEBOUNCE_DELAY)
            searchTracks(searchQuery)
        }
    }

    private fun searchTracks(query: String) {
        if (query.isEmpty()) return

        viewModelScope.launch {
            searchTracksInteractorImpl.execute(query).collect { tracks ->
                checkStateAfterSearch = true
                storyState = false
                _tracks.value = tracks
            }
        }
    }

    fun clearDateTrack() {
        _selectedTrack.value = null
    }

    override fun onCleared() {
        super.onCleared()
        _selectedTrack.value = null
    }

    fun getStoryState(): Boolean {
        return storyState
    }

    fun getStateAfterSearch(): Boolean {
        return checkStateAfterSearch
    }

    fun clearHistory() {
        manageSearchHistory.clearHistory()
        _tracks.value = emptyList()
        storyState = false
    }

    fun getQuery(): String {
        return searchQuery
    }

    private fun getHistoryTrack() {
        val historyTracks = manageSearchHistory.getSearchHistory()
        _tracks.value = historyTracks
        storyState = historyTracks.isNotEmpty()
    }
}