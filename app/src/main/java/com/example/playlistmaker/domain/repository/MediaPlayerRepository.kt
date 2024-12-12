package com.example.playlistmaker.domain.repository

interface MediaPlayerRepository {
    fun preparePlayer(url: String)
    fun play()
    fun pause()
    fun release()
    fun getCurrentPosition(): Int
    fun isPlaying(): Boolean
}