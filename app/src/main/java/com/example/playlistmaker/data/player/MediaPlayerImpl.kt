package com.example.playlistmaker.data.player

import android.media.MediaPlayer
import com.example.playlistmaker.domain.repository.MediaPlayerRepository

class MediaPlayerImpl(private var mediaPlayer: MediaPlayer?) : MediaPlayerRepository {

    override fun preparePlayer(url: String) {
        release()
        mediaPlayer = MediaPlayer().apply {
            setDataSource(url)
            prepareAsync()
        }
    }

    override fun play() {
        mediaPlayer?.start()
    }

    override fun pause() {
        mediaPlayer?.pause()
    }

    override fun release() {
        mediaPlayer?.release()
        mediaPlayer = null
    }

    override fun getCurrentPosition(): Int {
        return mediaPlayer?.currentPosition ?: 0
    }

    override fun isPlaying(): Boolean {
        return mediaPlayer?.isPlaying ?: false
    }
}