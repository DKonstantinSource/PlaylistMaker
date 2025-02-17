package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.MediaPlayerInteractor
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.domain.repository.MediaPlayerRepository

class MediaPlayerInteractorImpl(private val mediaPlayerRepository: MediaPlayerRepository) :
    MediaPlayerInteractor {

    override fun play() {
        mediaPlayerRepository.play()
    }

    override fun execute(track: Track) {
        mediaPlayerRepository.preparePlayer(track.previewUrl ?: "null")
        mediaPlayerRepository.play()
    }

    override fun pause() {
        mediaPlayerRepository.pause()
    }

    override fun stop() {
        mediaPlayerRepository.release()
    }

    override fun getCurrentPosition(): Int {
        return mediaPlayerRepository.getCurrentPosition()
    }

    override fun isPlaying(): Boolean {
        return mediaPlayerRepository.isPlaying()
    }

    override fun setOnTrackCompleteListener(listener: () -> Unit) {
        mediaPlayerRepository.setOnTrackCompleteListener(listener)
    }
}
