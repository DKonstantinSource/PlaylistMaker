package com.example.playlistmaker.data.player

import NetworkUtils
import android.media.MediaPlayer
import com.example.playlistmaker.domain.api.MediaPlayerInteractor
import com.example.playlistmaker.domain.impl.MediaPlayerInteractorImpl
import com.example.playlistmaker.domain.repository.MediaPlayerRepository
import com.example.playlistmaker.presentation.fragments.player.ScreenReceiver
import org.koin.dsl.module

val mediaPlayerModule = module {

    single { MediaPlayer() }
    single<MediaPlayerRepository> { MediaPlayerImpl(get()) }
    factory<MediaPlayerInteractor> { MediaPlayerInteractorImpl(get()) }
    single { ScreenReceiver() }
    single { NetworkUtils }
}