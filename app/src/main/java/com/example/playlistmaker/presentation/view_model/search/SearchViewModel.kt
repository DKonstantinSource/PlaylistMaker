package com.example.playlistmaker.presentation.view_model.search

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.Constants.SEARCH_DEBOUNCE_DELAY
import com.example.playlistmaker.domain.api.ManageSearchHistory
import com.example.playlistmaker.domain.impl.SearchTracksInteractorImpl
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.presentation.search.SearchStatus
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchViewModel(
    private val manageSearchHistory: ManageSearchHistory,
    private var searchTracksInteractorImpl: SearchTracksInteractorImpl,
    private val context: Context
) : ViewModel() {

    private val _tracks = MutableLiveData<List<Track>>()
    val tracks: LiveData<List<Track>> get() = _tracks

    private val _selectedTrack = MutableLiveData<Track?>()
    val selectedTrack: LiveData<Track?> get() = _selectedTrack

    private val _query = MutableLiveData("")
    val query: LiveData<String> = _query

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isErrorConnection = MutableLiveData(false)
    val isErrorConnection: LiveData<Boolean> = _isErrorConnection


    private val _searchStatus = MutableLiveData(SearchStatus.Idle)
    val searchStatus: LiveData<SearchStatus> = _searchStatus

    private val _storyState = mutableStateOf(false)
    val storyState = _storyState

    private var searchJob: Job? = null
    private var searchQuery: String = ""

    init {
        getHistoryTrack()
    }

    fun onSearchQueryChanged(query: String) {
        _query.value = query
        searchQuery = query
        if (query.isBlank()) {
            getHistoryTrack()
            _searchStatus.value = SearchStatus.Idle
        } else {
            searchDebounce()
        }
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

    private fun searchDebounce() {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(SEARCH_DEBOUNCE_DELAY)
            searchTracks(searchQuery)
        }
    }

    fun searchTracks(query: String) {
        if (query.isEmpty()) {
            _searchStatus.value = SearchStatus.Idle
            val history = manageSearchHistory.getSearchHistory().take(10)
            _tracks.value = history
            _storyState.value = history.isNotEmpty()
            return
        }

        if (!isInternetAvailable()) {
            _searchStatus.value = SearchStatus.Error
            _tracks.value = emptyList()
            _storyState.value = false
            return
        }

        _searchStatus.value = SearchStatus.Loading

        viewModelScope.launch {
            searchTracksInteractorImpl.execute(query).collect { tracks ->
                _tracks.value = tracks
                _searchStatus.value = SearchStatus.Success
                _storyState.value = false
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

    fun clearHistory() {
        manageSearchHistory.clearHistory()
        _tracks.value = emptyList()
        _storyState.value = false
    }

    fun getQuery(): String {
        return _query.value.orEmpty()
    }

    private fun getHistoryTrack() {
        val historyTracks = manageSearchHistory.getSearchHistory()
        _tracks.value = historyTracks.take(10)
        _storyState.value = historyTracks.isNotEmpty()
    }

    private fun isInternetAvailable(): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}
