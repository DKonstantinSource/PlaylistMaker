package com.example.playlistmaker.presentation.view_model.library

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.domain.model.Track

class LibraryViewModel : ViewModel() {
    private val _tracks = MutableLiveData<List<Track>>(emptyList())
    val tracks: LiveData<List<Track>> get() = _tracks

    private val _isFavoriteTabSelected = MutableLiveData<Boolean>(true)
    val isFavoriteTabSelected: LiveData<Boolean> get() = _isFavoriteTabSelected

    fun toggleTab(isFavorite: Boolean) {
        _isFavoriteTabSelected.value = isFavorite
    }

    fun setTracks(newTracks: List<Track>) {
        _tracks.value = newTracks
        // TODO В след спринте (Наверное) настанет твоеё время !
    }


}