package com.example.playlistmaker.presentation.fragments.player.service

import com.example.playlistmaker.domain.model.Track
import kotlinx.coroutines.flow.StateFlow

interface AudioPlayerServiceInterface {
    fun preparePlayer(track: Track)
    fun play()
    fun pause()
    fun stop()
    fun getIsPlaying(): StateFlow<Boolean>
    fun getCurrentTime(): StateFlow<String>
    fun togglePlayback()
    fun showNotification()

    fun showNotificationIfPlaying()
    fun hideNotification()
}
