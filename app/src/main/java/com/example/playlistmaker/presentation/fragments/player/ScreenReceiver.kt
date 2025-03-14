package com.example.playlistmaker.presentation.fragments.player

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class ScreenReceiver : BroadcastReceiver() {
    var playbackCallback: ((Boolean) -> Unit)? = null

    override fun onReceive(context: Context?, intent: Intent?) {

        if (intent?.action == Intent.ACTION_SCREEN_OFF) {
            playbackCallback?.invoke(true)
        }
    }
}