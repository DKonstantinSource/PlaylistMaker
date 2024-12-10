package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.model.PlayerControl
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.domain.use_case.MediaPlayerUseCase

class MediaPlayerImpl(private val mediaPlayerUseCase: MediaPlayerUseCase) {

    private val playerControl = PlayerControl()

    fun play() {
        mediaPlayerUseCase.play()
        playerControl.play()
    }

    fun execute(track: Track) {
        mediaPlayerUseCase.preparePlayer(track.previewUrl)
        mediaPlayerUseCase.play()
        playerControl.play()
    }

    fun pause() {
        mediaPlayerUseCase.pause()
        playerControl.pause()
    }

    fun stop() {
        mediaPlayerUseCase.release()
        playerControl.stop()
    }

    fun getCurrentPosition(): Int {
        return mediaPlayerUseCase.getCurrentPosition()
    }

    fun isPlaying(): Boolean {
        return playerControl.isPlaying()
    }

}