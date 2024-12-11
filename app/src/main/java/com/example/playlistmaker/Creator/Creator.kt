package com.example.playlistmaker.Creator

import android.content.Context
import android.content.SharedPreferences
import com.example.playlistmaker.data.impl.MediaPlayerImpl
import com.example.playlistmaker.domain.impl.ManageSearchHistoryInteractorImpl
import com.example.playlistmaker.data.impl.SearchHistoryImpl
import com.example.playlistmaker.data.impl.SettingsRepositoryImpl
import com.example.playlistmaker.data.impl.TrackRepositoryImpl
import com.example.playlistmaker.data.network.ApiService
import com.example.playlistmaker.data.network.RetrofitClient
import com.example.playlistmaker.domain.repository.SearchHistoryRepository
import com.example.playlistmaker.domain.repository.TrackRepository
import com.example.playlistmaker.domain.api.SettingsInteractor
import com.example.playlistmaker.domain.impl.SettingsInteractorImpl
import com.example.playlistmaker.domain.api.ManageSearchHistory
import com.example.playlistmaker.domain.api.MediaPlayerInteractor
import com.example.playlistmaker.domain.impl.MediaPlayerInteractorImpl
import com.example.playlistmaker.domain.impl.SearchTracksInteractorImpl


object Creator {

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

    fun createSettingsInteractor(context: Context): SettingsInteractor {
        return SettingsInteractorImpl(SettingsRepositoryImpl(context))
    }
}