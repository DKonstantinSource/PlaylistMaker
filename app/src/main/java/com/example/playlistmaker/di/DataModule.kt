package com.example.playlistmaker.di

import com.example.playlistmaker.presentation.fragments.player.service.AudioPlayerService
import com.example.playlistmaker.presentation.fragments.player.service.AudioPlayerServiceInterface
import com.example.playlistmaker.presentation.view_model.library.LibraryViewModel
import com.example.playlistmaker.presentation.view_model.player.PlayerViewModel
import com.example.playlistmaker.presentation.view_model.search.SearchViewModel
import com.example.playlistmaker.presentation.view_model.settings.SettingsViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


val dataModule = module {

    viewModel { SearchViewModel(get(), get(), androidContext()) }

    viewModel { PlayerViewModel(get(), get(), get(), get()) }

    viewModel { LibraryViewModel(get(), get(), get()) }
    viewModel { SettingsViewModel(get()) }

    single<AudioPlayerServiceInterface> { AudioPlayerService() }
}