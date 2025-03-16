package com.example.playlistmaker.di

import com.example.playlistmaker.presentation.view_model.library.LibraryViewModel
import com.example.playlistmaker.presentation.view_model.player.PlayerViewModel
import com.example.playlistmaker.presentation.view_model.search.SearchViewModel
import com.example.playlistmaker.presentation.view_model.settings.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


val dataModule = module {

    viewModel { SearchViewModel(get(), get()) }

    viewModel { PlayerViewModel(get(), get(), get(), get()) }

    viewModel { LibraryViewModel(get(), get()) }
    viewModel { SettingsViewModel(get()) }
}