package com.example.playlistmaker.presentation.fragments.player.service

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.model.Track
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AudioPlayerService : Service(), AudioPlayerServiceInterface {

    private val binder = AudioPlayerBinder()
    private var mediaPlayer: MediaPlayer? = null
    private var currentTrack: Track? = null

    private var isAppInBackground = false

    private val _isPlaying = MutableStateFlow(false)
    override fun getIsPlaying(): StateFlow<Boolean> = _isPlaying

    private val _currentTime = MutableStateFlow("00:00")
    override fun getCurrentTime(): StateFlow<String> = _currentTime

    private val updateHandler = Handler(Looper.getMainLooper())
    private val updateRunnable = object : Runnable {
        override fun run() {
            mediaPlayer?.let {
                val position = it.currentPosition
                _currentTime.value =
                    String.format("%02d:%02d", position / 60000, (position / 1000) % 60)
                if (it.isPlaying) updateHandler.postDelayed(this, 500)
            }
        }
    }

    inner class AudioPlayerBinder : Binder() {
        fun getService(): AudioPlayerServiceInterface = this@AudioPlayerService
    }

    override fun onBind(intent: Intent): IBinder {
        intent.getParcelableExtra<Track>("track")?.let {
            currentTrack = it
        }
        return binder
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()

        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_SCREEN_OFF)
            addAction(Intent.ACTION_USER_PRESENT)
        }
        registerReceiver(screenReceiver, filter)
    }

    override fun togglePlayback() {
        if (_isPlaying.value) pause() else play()
    }

    override fun preparePlayer(track: Track) {
        currentTrack = track
        mediaPlayer = MediaPlayer().apply {
            setDataSource(track.previewUrl)
            prepareAsync()
            setOnPreparedListener {
                updateHandler.post(updateRunnable)
            }
            setOnCompletionListener {
                stopForeground(true)
                _isPlaying.value = false
                _currentTime.value = "00:00"
            }
        }
    }

    private val screenReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                Intent.ACTION_SCREEN_OFF -> {
                    isAppInBackground = true
                    if (_isPlaying.value) showNotification()
                }

                Intent.ACTION_USER_PRESENT -> {
                    isAppInBackground = false
                    stopForeground(true)
                }
            }
        }
    }

    override fun play() {
        mediaPlayer?.let {
            if (!it.isPlaying) {
                it.start()
                _isPlaying.value = true
                updateHandler.post(updateRunnable)
            }
        }
    }

    override fun pause() {
        mediaPlayer?.pause()
        _isPlaying.value = false
        stopForeground(true)
    }

    override fun stop() {
        mediaPlayer?.release()
        mediaPlayer = null
        stopForeground(true)
        _isPlaying.value = false
        _currentTime.value = "00:00"
    }

    override fun onDestroy() {
        super.onDestroy()
        stop()
        unregisterReceiver(screenReceiver)
    }

    @SuppressLint("MissingPermission")
    override fun showNotification() {
        val notification = NotificationCompat.Builder(this, "audio_channel")
            .setContentTitle("Playlist Maker")
            .setContentText("${currentTrack?.artistName} - ${currentTrack?.trackName}")
            .setSmallIcon(R.drawable.image_placeholder)
            .setOngoing(true)
            .setAutoCancel(false)
            .build()

        startForeground(1, notification)
    }

    override fun showNotificationIfPlaying() {
        if (_isPlaying.value) {
            startForeground(1, createNotification())
        }
    }

    override fun hideNotification() {
        stopForeground(true)
    }

    private fun createNotification(): Notification {
        val channelId = "audio_channel"

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("Музыка играет")
            .setContentText("${currentTrack?.artistName} - ${currentTrack?.trackName}")
            .setSmallIcon(R.drawable.image_placeholder)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .setAutoCancel(false)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "audio_channel",
                "Audio Playback",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Notifications for audio playback"
            }

            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }
}
