package com.example.playlistmaker.domain.use_case

interface MediaPlayerUseCase {
    fun preparePlayer(url: String)
    fun play()
    fun pause()
    fun release()
    fun getCurrentPosition(): Int
    fun isPlaying(): Boolean
}