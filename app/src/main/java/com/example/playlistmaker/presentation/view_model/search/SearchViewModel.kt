package com.example.playlistmaker.presentation.view_model.search


import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.Constants.SEARCH_DEBOUNCE_DELAY
import com.example.playlistmaker.domain.api.ManageSearchHistory
import com.example.playlistmaker.domain.impl.SearchTracksInteractorImpl
import com.example.playlistmaker.domain.model.Track


class SearchViewModel(
    private val manageSearchHistory: ManageSearchHistory,
    private var searchTracksInteractorImpl: SearchTracksInteractorImpl
) : ViewModel() {

    private val _tracks = MutableLiveData<List<Track>>()
    val tracks: LiveData<List<Track>> get() = _tracks

    private val _selectedTrack = MutableLiveData<Track?>()
    val selectedTrack: LiveData<Track?> get() = _selectedTrack

    private val handler = Handler(Looper.getMainLooper())
    private var searchRunnable: Runnable? = null
    private var searchQuery: String = ""
    private var storyState = true
    private var checkStateAfterSearch = false

    init {
        getHistoryTrack()
    }

    fun trackClicked(track: Track) {
        manageSearchHistory.addToHistory(track)
        _selectedTrack.value = track
    }

    fun onSearchQueryChanged(query: String) {
        searchQuery = query
        if (searchQuery.isBlank() or searchQuery.isEmpty()) {
            getHistoryTrack()
            return
        } else {
            searchDebounce()
        }

    }

    private fun searchDebounce() {
        searchRunnable?.let { handler.removeCallbacks(it) }
        searchRunnable = Runnable {
            searchTracks(searchQuery)
        }
        handler.postDelayed(searchRunnable!!, SEARCH_DEBOUNCE_DELAY)
    }

    private fun searchTracks(query: String) {
        if (query.isEmpty() or query.isBlank()) {
            return
        }

        searchTracksInteractorImpl.execute(query) { tracks ->
            checkStateAfterSearch = true
            storyState = false
            _tracks.value = tracks!!
        }
    }

    fun getStoryState(): Boolean {
        return storyState
    }

    fun getStateAfterSearch(): Boolean {
        return checkStateAfterSearch
    }

    override fun onCleared() {
        super.onCleared()
        handler.removeCallbacksAndMessages(null)
    }

    fun clearHistory() {
        manageSearchHistory.clearHistory()
        _tracks.value = emptyList()
        storyState = false
    }

    fun getQuery(): String {
        return searchQuery
    }

    private fun getHistoryTrack(): Unit {
        val historyTracks = manageSearchHistory.getSearchHistory()
        _tracks.value = historyTracks
        storyState = historyTracks.isNotEmpty()
    }

}