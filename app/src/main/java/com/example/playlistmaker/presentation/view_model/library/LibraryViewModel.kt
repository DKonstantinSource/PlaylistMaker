package com.example.playlistmaker.presentation.view_model.library

import android.util.Log
import androidx.lifecycle.*
import com.example.playlistmaker.domain.impl.FavoriteTracksInteractor
import com.example.playlistmaker.domain.model.Track
import kotlinx.coroutines.launch

class LibraryViewModel(private val favoriteTracksInteractor: FavoriteTracksInteractor) :
    ViewModel() {

    private val _tracks = MutableLiveData<List<Track>>()
    val tracks: LiveData<List<Track>> = _tracks


    private val _selectedTrack = MutableLiveData<Track?>()
    val selectedTrack: LiveData<Track?> get() = _selectedTrack

    init {
        loadFavoriteTracks()
    }

    fun clearSelectedTrack() {
        _selectedTrack.value = null
    }

    fun loadFavoriteTracks() {
        viewModelScope.launch {
            favoriteTracksInteractor.getFavoriteTracks().collect { favoriteTracks ->

            _tracks.value = favoriteTracks
                Log.d("LibraryViewModel", "Loaded tracks: ${favoriteTracks.size}")
            }
        }
    }

    fun trackClicked(track: Track) {
        _selectedTrack.value = track
    }
}
