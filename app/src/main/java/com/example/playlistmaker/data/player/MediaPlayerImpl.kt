package com.example.playlistmaker.data.player

import android.media.MediaPlayer
import android.util.Log
import com.example.playlistmaker.domain.repository.MediaPlayerRepository

class MediaPlayerImpl(private var mediaPlayer: MediaPlayer?) : MediaPlayerRepository {

    private var onCompletionListener: (() -> Unit)? = null
    private var isPrepared = false

    override fun preparePlayer(url: String) {
        Log.d("MediaPlayerImpl", "Preparing MediaPlayer for track: $url")
        release()
        mediaPlayer = MediaPlayer().apply {
            setDataSource(url)
            prepareAsync()
            setOnPreparedListener {
                isPrepared = true
                Log.d("MediaPlayerImpl", "MediaPlayer is prepared")
            }
            setOnCompletionListener {
                val currentPos = mediaPlayer?.currentPosition ?: 0
                val duration = mediaPlayer?.duration ?: 0

                Log.d(
                    "MediaPlayerImpl",
                    "onCompletion() called at position: $currentPos, duration: $duration"
                )

                if (duration - currentPos > 500) {
                    Log.d("MediaPlayerImpl", "Ignoring false onCompletion call")
                    return@setOnCompletionListener
                }

                mediaPlayer?.seekTo(0)
                onCompletionListener?.invoke()
            }
        }
    }


    override fun play() {
        if (isPrepared) {
            mediaPlayer?.start()
        }
    }

    override fun pause() {
        if (mediaPlayer?.isPlaying == true) {
            mediaPlayer?.pause()
        }
    }

    override fun release() {
        mediaPlayer?.release()
        mediaPlayer = null
        isPrepared = false
    }

    override fun getCurrentPosition(): Int {
        val position = if (isPrepared) mediaPlayer?.currentPosition ?: 0 else 0
        Log.d("MediaPlayerImpl", "getCurrentPosition(): $position")
        return position
    }


    override fun isPlaying(): Boolean {
        return mediaPlayer?.isPlaying ?: false
    }

    override fun setOnTrackCompleteListener(listener: () -> Unit) {
        onCompletionListener = listener
    }
}
