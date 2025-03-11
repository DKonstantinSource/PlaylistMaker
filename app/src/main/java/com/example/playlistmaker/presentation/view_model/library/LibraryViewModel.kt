package com.example.playlistmaker.presentation.view_model.library

import androidx.lifecycle.*
import com.example.playlistmaker.domain.api.PlaylistInteractor
import com.example.playlistmaker.domain.impl.FavoriteTracksInteractor
import com.example.playlistmaker.domain.model.Playlist
import com.example.playlistmaker.domain.model.Track
import kotlinx.coroutines.launch

class LibraryViewModel(
    private val favoriteTracksInteractor: FavoriteTracksInteractor,
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    private val _tracks = MutableLiveData<List<Track>>()
    val tracks: LiveData<List<Track>> = _tracks

    private val _playlists = MutableLiveData<List<Playlist>>()
    val playlists: LiveData<List<Playlist>> = _playlists

    private val _selectedTrack = MutableLiveData<Track?>()
    val selectedTrack: LiveData<Track?> get() = _selectedTrack

    init {
        loadFavoriteTracks()
        loadPlaylists()
    }

    fun createPlaylist(playlist: Playlist) {
        viewModelScope.launch {
            playlistInteractor.createPlaylist(playlist)
            loadPlaylists()
        }
    }

    fun updatePlaylist(playlist: Playlist) {
        viewModelScope.launch {
            playlistInteractor.updatePlaylist(playlist)
            loadPlaylists()
        }
    }

    fun clearSelectedTrack() {
        _selectedTrack.value = null
    }

    fun loadFavoriteTracks() {
        viewModelScope.launch {
            favoriteTracksInteractor.getFavoriteTracks().collect { favoriteTracks ->
                _tracks.value = favoriteTracks
            }
        }
    }

    fun loadPlaylists() {
        viewModelScope.launch {
            _playlists.value = playlistInteractor.getAllPlaylists()
        }
    }

    fun trackClicked(track: Track) {
        _selectedTrack.value = track
    }
}
