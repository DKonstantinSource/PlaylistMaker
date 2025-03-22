package com.example.playlistmaker.presentation.view_model.library

import android.util.Log
import androidx.lifecycle.*
import com.example.playlistmaker.domain.api.PlaylistInteractor
import com.example.playlistmaker.domain.api.TrackAddToPlaylistInteractor
import com.example.playlistmaker.domain.impl.FavoriteTracksInteractor
import com.example.playlistmaker.domain.model.Playlist
import com.example.playlistmaker.domain.model.Track
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

class LibraryViewModel(
    private val favoriteTracksInteractor: FavoriteTracksInteractor,
    private val playlistInteractor: PlaylistInteractor,
    private val trackAddToPlaylistInteractor: TrackAddToPlaylistInteractor

) : ViewModel() {

    private val _tracks = MutableLiveData<List<Track>>()
    val tracks: LiveData<List<Track>> = _tracks

    private val _playlists = MutableLiveData<List<Playlist>>()
    val playlists: LiveData<List<Playlist>> = _playlists

    private val _selectedTrack = MutableLiveData<Track?>()
    val selectedTrack: LiveData<Track?> get() = _selectedTrack

    private val _playlistCreated = MutableLiveData<Boolean>()
    val playlistCreated: LiveData<Boolean> get() = _playlistCreated

    private val _lastCreatedPlaylistName = MutableLiveData<String?>()
    val lastCreatedPlaylistName: LiveData<String?> get() = _lastCreatedPlaylistName

    private val _currentPlaylist = MutableLiveData<Playlist?>()
    val currentPlaylist: LiveData<Playlist?> = _currentPlaylist

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private var trackTimeAndCount: String = ""


    init {


        loadFavoriteTracks()
        loadPlaylists()
    }


    fun updatePlaylist(playlist: Playlist) {
        viewModelScope.launch {
            playlistInteractor.updatePlaylist(playlist)
            loadPlaylists()
        }
    }

    fun deletePlaylist(playlistId: Long) {
        viewModelScope.launch {
            try {
                playlistInteractor.deletePlaylist(playlistId)
            } catch (e: CancellationException) {
                Log.e("DeletePlaylist", "Job was cancelled: ${e.message}")
            } catch (e: Exception) {
                Log.e("DeletePlaylist", "Error: ${e.message}")
            }
            //TODO now dat stay , but if all work on UI flow , delete message

        }
    }

    fun loadTracksForPlaylist(playlistId: Long) {
        viewModelScope.launch {
            val tracks = trackAddToPlaylistInteractor.getTracksForPlaylist(playlistId)
            _tracks.postValue(tracks)
            updateTrackTimeAndCount(tracks)
        }
    }


    fun removeTrackFromPlaylist(playlistId: Long, trackId: Long) {
        viewModelScope.launch {
            trackAddToPlaylistInteractor.removeTrackFromPlaylist(playlistId, trackId)
            loadPlaylist(playlistId)
        }
    }


    fun loadPlaylist(playlistId: Long) {
        Log.d(
            "DEBUG_TRACKS",
            "Trying to load playlist with ID: $playlistId"
        )  // Лог для проверки, что метод вызывается
        viewModelScope.launch {
            _isLoading.postValue(true)
            val playlist = playlistInteractor.getPlaylistById(playlistId)
            if (playlist == null) {
                Log.e("DEBUG_TRACKS", "Failed to load playlist for ID: $playlistId")
            } else {
                Log.d(
                    "DEBUG_TRACKS",
                    "Loaded playlist: $playlist"
                )
            }
            _currentPlaylist.postValue(playlist)
            _tracks.postValue(playlist?.tracks)
            _isLoading.postValue(false)
        }
    }


    fun createPlaylist(playlist: Playlist) {
        viewModelScope.launch {
            playlistInteractor.createPlaylist(playlist)
            loadPlaylists()
            _lastCreatedPlaylistName.postValue(playlist.name)
            _playlistCreated.postValue(true)
        }
    }

    private fun updateTrackTimeAndCount(tracks: List<Track>) {
        val totalTimeMillis = tracks.sumOf { it.trackTimeMillis }
        val totalTimeSeconds = totalTimeMillis / 1000
        val minutes = totalTimeSeconds / 60
        val seconds = totalTimeSeconds % 60
        trackTimeAndCount = String.format("%02d:%02d • %d треков", minutes, seconds, tracks.size)
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
