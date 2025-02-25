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

    private val _isFavoriteTabSelected = MutableLiveData(true)
    val isFavoriteTabSelected: LiveData<Boolean> get() = _isFavoriteTabSelected

    private val _isEmpty = MutableLiveData<Boolean>(false)
    val isEmpty: LiveData<Boolean> = _isEmpty

    init {
        loadFavoriteTracks()
    }

    fun toggleTab(isFavorite: Boolean) {
        _isFavoriteTabSelected.value = isFavorite
    }

    fun loadFavoriteTracks() {
        viewModelScope.launch {
            favoriteTracksInteractor.getFavoriteTracks().collect { favoriteTracks ->
                _tracks.value = favoriteTracks
                _isEmpty.value = favoriteTracks.isEmpty()
            }
        }
    }
}
