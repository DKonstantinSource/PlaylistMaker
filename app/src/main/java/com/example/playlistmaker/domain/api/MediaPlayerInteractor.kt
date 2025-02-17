package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.model.Track

interface MediaPlayerInteractor {
    fun play()
    fun execute(track: Track)
    fun pause()
    fun stop()
    fun getCurrentPosition(): Int
    fun isPlaying(): Boolean
    fun setOnTrackCompleteListener(listener: () -> Unit)
}
