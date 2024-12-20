package com.example.playlistmaker.Creator

import android.app.Application
import android.content.SharedPreferences
import com.example.playlistmaker.data.impl.MediaPlayerImpl
import com.example.playlistmaker.domain.impl.ManageSearchHistoryInteractorImpl
import com.example.playlistmaker.data.impl.SearchHistoryImpl
import com.example.playlistmaker.data.settings.theme_settings.repository.SettingsRepositoryImpl
import com.example.playlistmaker.data.impl.TrackRepositoryImpl
import com.example.playlistmaker.data.network.API.ApiService
import com.example.playlistmaker.data.network.client.RetrofitClient
import com.example.playlistmaker.data.settings.sharing.imp.ExternalNavigatorImpl
import com.example.playlistmaker.domain.repository.SearchHistoryRepository
import com.example.playlistmaker.domain.repository.TrackRepository
import com.example.playlistmaker.domain.settings.theme_preference.api.SettingsInteractor
import com.example.playlistmaker.domain.settings.theme_preference.impl.SettingsInteractorImpl
import com.example.playlistmaker.domain.api.ManageSearchHistory
import com.example.playlistmaker.domain.api.MediaPlayerInteractor
import com.example.playlistmaker.domain.impl.MediaPlayerInteractorImpl
import com.example.playlistmaker.domain.impl.SearchTracksInteractorImpl
import com.example.playlistmaker.domain.settings.sharing.repository.ExternalNavigator
import com.example.playlistmaker.domain.settings.sharing.api.ExternalNavigatorInteractor
import com.example.playlistmaker.domain.settings.sharing.impl.ExternalNavigatorInteractorImpl


object Creator {

    lateinit var context: Application

    fun initApplication(application: Application) {
        this.context = application
    }

    fun createPlayer(): MediaPlayerInteractor {
        val mediaPlayerRepository = MediaPlayerImpl()
        return MediaPlayerInteractorImpl(mediaPlayerRepository)
    }

    private fun createSearchHistoryRepository(sharedPreferences: SharedPreferences): SearchHistoryRepository {
        return SearchHistoryImpl(sharedPreferences)
    }

    fun createManageSearchHistoryUseCase(sharedPreferences: SharedPreferences): ManageSearchHistory {
        val repository = createSearchHistoryRepository(sharedPreferences)
        return ManageSearchHistoryInteractorImpl(repository)
    }

    private val apiService: ApiService = RetrofitClient.createApiService()

    private fun createTrackRepository(): TrackRepository {
        return TrackRepositoryImpl(apiService)
    }

    fun createSearchTracksUseCase(): SearchTracksInteractorImpl {
        val repository = createTrackRepository()
        return SearchTracksInteractorImpl(repository)
    }

    fun createSettingsInteractor(): SettingsInteractor {
        return SettingsInteractorImpl(SettingsRepositoryImpl(context))
    }

    fun createExternalNavigatorInteractor(): ExternalNavigatorInteractor {
        return ExternalNavigatorInteractorImpl(createExternalNavigatorRepository())
    }

    private fun createExternalNavigatorRepository(): ExternalNavigator {
        return createExternalNavigatorUseCase()
    }

    private fun createExternalNavigatorUseCase(): ExternalNavigator {
        return ExternalNavigatorImpl(Creator.context)
    }


}