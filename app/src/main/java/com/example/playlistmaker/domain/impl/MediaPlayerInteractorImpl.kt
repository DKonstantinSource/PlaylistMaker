package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.MediaPlayerInteractor
import com.example.playlistmaker.domain.model.PlayerControl
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.domain.repository.MediaPlayerRepository

class MediaPlayerInteractorImpl(private val mediaPlayerRepository: MediaPlayerRepository) :
    MediaPlayerInteractor {

    private val playerControl = PlayerControl()

    override fun play() {
        mediaPlayerRepository.play()
        playerControl.play()
    }

    override fun execute(track: Track) {
        mediaPlayerRepository.preparePlayer(track.previewUrl)
        mediaPlayerRepository.play()
        playerControl.play()
    }

    override fun pause() {
        mediaPlayerRepository.pause()
        playerControl.pause()
    }

    override fun stop() {
        mediaPlayerRepository.release()
        playerControl.stop()
    }

    override fun getCurrentPosition(): Int {
        return mediaPlayerRepository.getCurrentPosition()
    }

    override fun isPlaying(): Boolean {
        return playerControl.isPlaying()
    }
}