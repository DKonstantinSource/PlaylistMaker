package com.example.playlistmaker.presentation.view_model.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.domain.api.MediaPlayerInteractor

class ViewModelFactory(private val mediaPlayerInteractor: MediaPlayerInteractor) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlayerViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST PlayerFactory!!!")
            return PlayerViewModel(mediaPlayerInteractor) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class CHECK !!!")
    }
}