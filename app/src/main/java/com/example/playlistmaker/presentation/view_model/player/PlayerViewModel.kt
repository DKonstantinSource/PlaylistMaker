package com.example.playlistmaker.presentation.view_model.player

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.Constants.DEFAULT_TIME_PLAYER
import com.example.playlistmaker.domain.api.MediaPlayerInteractor
import com.example.playlistmaker.domain.api.PlaylistInteractor
import com.example.playlistmaker.domain.api.TrackAddToPlaylistInteractor
import com.example.playlistmaker.domain.impl.FavoriteTracksInteractor
import com.example.playlistmaker.domain.model.PlayerState
import com.example.playlistmaker.domain.model.Playlist
import com.example.playlistmaker.domain.model.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PlayerViewModel(
    private val mediaPlayerInteractor: MediaPlayerInteractor,
    private val favoriteTracksInteractor: FavoriteTracksInteractor,
    private val playlistInteractor: PlaylistInteractor,
    private val trackAddToPlaylistInteractor: TrackAddToPlaylistInteractor,
) : ViewModel() {

    private val _trackInfo = MutableLiveData<Track>()
    val trackInfo: LiveData<Track> get() = _trackInfo

    private val _currentTrackTime = MutableLiveData<String>()
    val currentTrackTime: LiveData<String> get() = _currentTrackTime

    private val _isPlayingLiveData = MutableLiveData<Boolean>()
    val isPlayingLiveData: LiveData<Boolean> get() = _isPlayingLiveData

    private val _isFavorite = MutableLiveData<Boolean>()
    val isFavorite: LiveData<Boolean> get() = _isFavorite

    private val _playlists = MutableLiveData<List<Playlist>>()
    val playlists: LiveData<List<Playlist>> get() = _playlists

    private val _addTrackStatus = MutableLiveData<String?>()
    val addTrackStatus: LiveData<String?> get() = _addTrackStatus


    init {
        refreshPlaylists()
    }

    fun removeTrackFromPlaylist(playlistId: Long, trackId: Long) {
        viewModelScope.launch {
            playlistInteractor.removeTrackFromPlaylist(playlistId, trackId)
        }
    }


    fun addTrackToPlaylist(track: Track, playlistId: Long) {
        viewModelScope.launch {
            try {
                val playlist = playlistInteractor.getPlaylistById(playlistId)
                val playlistName = playlist?.name ?: "Неизвестный плейлист"

                val exists =
                    trackAddToPlaylistInteractor.isTrackInPlaylist(playlistId, track.trackId)
                if (exists) {
                    _addTrackStatus.postValue("Трек уже добавлен в плейлист \"$playlistName\".")
                    clearAddTrackStatusAfterDelay()
                    return@launch
                }

                trackAddToPlaylistInteractor.addTrackToPlaylist(track, playlistId)
                _addTrackStatus.postValue("Добавлено в плейлист \"$playlistName\"")
                refreshPlaylists()
                clearAddTrackStatusAfterDelay()
            } catch (e: Exception) {
                _addTrackStatus.postValue("Ошибка при добавлении трека")
                clearAddTrackStatusAfterDelay()
            }
        }
    }

    private fun clearAddTrackStatusAfterDelay() {
        viewModelScope.launch {
            delay(1000)
            _addTrackStatus.postValue(null)
        }
    }

    //TODO Валера скоро настанет твоё время =)
//    fun removeTrackFromPlaylist(playlistId: Int, trackId: Long) {
//        viewModelScope.launch {
//            try {
//                trackAddToPlaylistInteractor.removeTrackFromPlaylist(playlistId, trackId)
//                _trackStatus.postValue("Трек удалён из плейлиста")
//            } catch (e: Exception) {
//                _trackStatus.postValue("Ошибка при удалении трека")
//            }
//        }
//    }


    private var timerJob: Job? = null
    private var playerState = PlayerState.DEFAULT

    //TODO точка запроса репозитория
    fun refreshPlaylists() {
        viewModelScope.launch {
            _playlists.value = playlistInteractor.getAllPlaylists()
        }
    }


    fun setTrack(track: Track) {
        if (_trackInfo.value == track) return
        _trackInfo.value = track
        preparePlayer(track)
        checkIfFavorite(track.trackId)
    }

    private fun checkIfFavorite(trackId: Long) {
        viewModelScope.launch {
            favoriteTracksInteractor.getFavoriteTrackIds().collect { favoriteIds ->
                _isFavorite.postValue(favoriteIds.contains(trackId))
            }
        }
    }


    fun onFavoriteClicked() {
        val track = _trackInfo.value ?: return
        val isCurrentlyFavorite = _isFavorite.value ?: false
        viewModelScope.launch {
            try {
                if (isCurrentlyFavorite) {
                    favoriteTracksInteractor.removeTrack(track)
                } else {
                    favoriteTracksInteractor.addTrack(track)
                }
                withContext(Dispatchers.Main) {
                    _isFavorite.value = !isCurrentlyFavorite
                }
                Log.d(
                    "FavoriteTrack",
                    "Track ${track.trackName} ${if (isCurrentlyFavorite) "removed from" else "added to"} favorites"
                )
            } catch (e: Exception) {
                Log.e("FavoriteTrack", "Error updating favorite status for ${track.trackName}", e)
            }
        }
    }





    private fun preparePlayer(track: Track) {
        val songBridge = track.previewUrl
        if (!songBridge.isNullOrEmpty()) {
            Log.d("MediaPlayerImpl", "Preparing MediaPlayer for track: ${track.trackName}")
            mediaPlayerInteractor.execute(track)
            mediaPlayerInteractor.setOnTrackCompleteListener { onTrackCompleted() }
            playerState = PlayerState.PREPARED
        }
    }


    fun playbackControl() {
        Log.d("MediaPlayerImpl", "playbackControl() called - state: $playerState")
        when (playerState) {
            PlayerState.PLAYING -> pausePlayer()
            PlayerState.PREPARED, PlayerState.PAUSED -> startPlayer()
            PlayerState.DEFAULT -> {}
        }
    }

    private fun startPlayer() {
        if (playerState != PlayerState.PREPARED && playerState != PlayerState.PAUSED) return
        mediaPlayerInteractor.play()
        startCountdown()
        playerState = PlayerState.PLAYING
        _isPlayingLiveData.postValue(true)
    }

    fun pausePlayer() {
        mediaPlayerInteractor.pause()
        stopCountdown()
        playerState = PlayerState.PAUSED
        _isPlayingLiveData.postValue(false)
    }

    private fun startCountdown() {
        stopCountdown()
        timerJob = viewModelScope.launch(Dispatchers.Main) {
            Log.d("MediaPlayerImpl", "Waiting track to start ")

            while (!mediaPlayerInteractor.isPlaying()) {
                delay(100)
            }

            Log.d("MediaPlayerImpl", "startCountdown-- started - track is playing and so god")

            while (mediaPlayerInteractor.isPlaying()) {
                val currentPositionMillis = mediaPlayerInteractor.getCurrentPosition()
                val minutes = (currentPositionMillis / 1000) / 60
                val seconds = (currentPositionMillis / 1000) % 60
                val formattedTime = String.format("%02d:%02d", minutes, seconds)
                _currentTrackTime.postValue(formattedTime)
                delay(300)
            }

            Log.d("MediaPlayerImpl", "Timer stopped, if that, OMG RESOLVE ")
        }
    }


    private fun stopCountdown() {
        Log.d("MediaPlayerImpl", "stopCountdown() called")
        timerJob?.cancel()
        timerJob = null
    }


    private fun onTrackCompleted() {
        Log.d("MediaPlayerImpl", "Track completed event received")
        stopCountdown()
        _currentTrackTime.postValue(DEFAULT_TIME_PLAYER)
        _isPlayingLiveData.value = false
        playerState = PlayerState.PAUSED
    }


    fun cleanup() {
        stopCountdown()
        timerJob?.cancel()
        timerJob = null
        mediaPlayerInteractor.stop()
    }
}
