package com.example.playlistmaker.di

import NetworkUtils
import android.content.Context
import com.example.playlistmaker.Constants.BASE_URL_ITUNES
import com.example.playlistmaker.Constants.THEME_PREFERENCE
import com.example.playlistmaker.data.impl.MediaPlayerImpl
import com.example.playlistmaker.data.impl.SearchHistoryImpl
import com.example.playlistmaker.data.impl.TrackRepositoryImpl
import com.example.playlistmaker.data.network.API.ApiService
import com.example.playlistmaker.data.settings.sharing.imp.ExternalNavigatorImpl
import com.example.playlistmaker.data.settings.theme_settings.repository.SettingsRepositoryImpl
import com.example.playlistmaker.domain.api.ManageSearchHistory
import com.example.playlistmaker.domain.api.MediaPlayerInteractor
import com.example.playlistmaker.domain.impl.ManageSearchHistoryInteractorImpl
import com.example.playlistmaker.domain.impl.MediaPlayerInteractorImpl
import com.example.playlistmaker.domain.impl.SearchTracksInteractorImpl
import com.example.playlistmaker.domain.repository.MediaPlayerRepository
import com.example.playlistmaker.domain.repository.SearchHistoryRepository
import com.example.playlistmaker.domain.repository.TrackRepository
import com.example.playlistmaker.domain.settings.sharing.api.ExternalNavigatorInteractor
import com.example.playlistmaker.domain.settings.sharing.impl.ExternalNavigatorInteractorImpl
import com.example.playlistmaker.domain.settings.sharing.repository.ExternalNavigator
import com.example.playlistmaker.domain.settings.theme_preference.api.SettingsInteractor
import com.example.playlistmaker.domain.settings.theme_preference.impl.SettingsInteractorImpl
import com.example.playlistmaker.domain.settings.theme_preference.repository.SettingsRepository
import com.example.playlistmaker.presentation.ui.player.ScreenReceiver
import com.example.playlistmaker.presentation.view_model.main.MainViewModel
import com.example.playlistmaker.presentation.view_model.player.PlayerViewModel
import com.example.playlistmaker.presentation.view_model.search.SearchViewModel
import com.example.playlistmaker.presentation.view_model.settings.SettingsViewModel
import com.google.gson.Gson
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


val dataModule = module {
    single {
        Retrofit.Builder()
            .baseUrl(BASE_URL_ITUNES)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    single { get<Retrofit>().create(ApiService::class.java) }
    single { Gson() }

    single { androidApplication().getSharedPreferences(THEME_PREFERENCE, Context.MODE_PRIVATE) }

    single<SearchHistoryRepository> { SearchHistoryImpl(get()) }

    factory<ManageSearchHistory> { ManageSearchHistoryInteractorImpl(get()) }

    factory<TrackRepository> { TrackRepositoryImpl(get()) }

    factory { SearchTracksInteractorImpl(get()) }

    factory<SettingsRepository> { SettingsRepositoryImpl(get()) }
    factory<SettingsInteractor> { SettingsInteractorImpl(get()) }

    single<ExternalNavigatorInteractor> { ExternalNavigatorInteractorImpl(get()) }

    single<ExternalNavigator> { ExternalNavigatorImpl(get()) }

    factory<MediaPlayerRepository> { MediaPlayerImpl() }
    factory<MediaPlayerInteractor> { MediaPlayerInteractorImpl(get()) }
    single { ScreenReceiver() }


    //TODO Valera Nastalo Tvoe Vremya
    single { NetworkUtils }


    viewModel { SearchViewModel(get(), get()) }
    viewModel { PlayerViewModel(get()) }
    viewModel { SettingsViewModel(get()) }
    viewModel { MainViewModel() }
}