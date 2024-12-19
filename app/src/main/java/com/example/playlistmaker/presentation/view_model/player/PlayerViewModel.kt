package com.example.playlistmaker.presentation.view_model.player


import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.domain.api.MediaPlayerInteractor
import com.example.playlistmaker.domain.model.PlayerState
import com.example.playlistmaker.domain.model.Track

class PlayerViewModel(private val mediaPlayerInteractor: MediaPlayerInteractor) : ViewModel() {

    private val _trackInfo = MutableLiveData<Track>()
    val trackInfo: LiveData<Track> get() = _trackInfo

    private val _currentTrackTime = MutableLiveData<String>()
    val currentTrackTime: LiveData<String> get() = _currentTrackTime

    private var playerState = PlayerState.DEFAULT
    private var handler: Handler? = null

    fun setTrack(track: Track) {
        _trackInfo.value = track
        preparePlayer(track)
    }

    private fun preparePlayer(track: Track) {
        val songBridge = track.previewUrl
        if (!songBridge.isNullOrEmpty()) {
            mediaPlayerInteractor.execute(track)
            playerState = PlayerState.PREPARED
        }
    }


    private fun startPlayer() {
        mediaPlayerInteractor.play()
        startCountdown()
        playerState = PlayerState.PLAYING
    }

    private fun pausePlayer() {
        mediaPlayerInteractor.pause()
        playerState = PlayerState.PAUSED
    }

    fun isPlaying(): Boolean {
        return playerState == PlayerState.PLAYING
    }


    fun playbackControl() {
        when (playerState) {
            PlayerState.PLAYING -> pausePlayer()
            PlayerState.PREPARED, PlayerState.PAUSED -> startPlayer()
            PlayerState.DEFAULT -> {
                Log.e("ErrorState", "PlayerErrorState")
            }
        }
    }


    private fun startCountdown() {
        handler = Handler(Looper.getMainLooper())
        handler?.post(object : Runnable {
            @SuppressLint("DefaultLocale")
            override fun run() {
                if (mediaPlayerInteractor.isPlaying()) {
                    val currentPositionMillis = mediaPlayerInteractor.getCurrentPosition()
                    val minutes = (currentPositionMillis / 1000) / 60
                    val seconds = (currentPositionMillis / 1000) % 60
                    val formattedTime = String.format("%02d:%02d", minutes, seconds)
                    _currentTrackTime.postValue(formattedTime)
                    handler?.postDelayed(this, 1000)
                } else {
                    handler?.removeCallbacks(this)
                }
            }
        })
    }

    fun cleanup() {
        handler?.removeCallbacksAndMessages(null)
    }
}