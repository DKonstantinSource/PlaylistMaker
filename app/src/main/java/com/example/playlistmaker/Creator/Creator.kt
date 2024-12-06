package com.example.playlistmaker.Creator

import android.content.Context
import android.content.SharedPreferences
import com.example.playlistmaker.data.impl.ManageSearchHistoryUseCaseImpl
import com.example.playlistmaker.data.impl.SearchHistory
import com.example.playlistmaker.data.impl.SettingsRepositoryImpl
import com.example.playlistmaker.data.impl.TrackRepositoryImpl
import com.example.playlistmaker.data.network.ApiService
import com.example.playlistmaker.data.network.RetrofitClient
import com.example.playlistmaker.domain.repository.SearchHistoryRepository
import com.example.playlistmaker.domain.repository.TrackRepository
import com.example.playlistmaker.domain.api.SettingsInteractor
import com.example.playlistmaker.domain.impl.SettingsInteractorImpl
import com.example.playlistmaker.domain.use_case.ManageSearchHistoryUseCase

import com.example.playlistmaker.domain.use_case.SearchTracksUseCase


object Creator {
    fun createSearchHistoryRepository(sharedPreferences: SharedPreferences): SearchHistoryRepository {
        return SearchHistory(sharedPreferences)
    }

    fun createManageSearchHistoryUseCase(sharedPreferences: SharedPreferences): ManageSearchHistoryUseCase {
        val repository = createSearchHistoryRepository(sharedPreferences)
        return ManageSearchHistoryUseCaseImpl(repository)
    }

    private val apiService: ApiService = RetrofitClient.createApiService()

    fun createTrackRepository(): TrackRepository {
        return TrackRepositoryImpl(apiService)
    }

    fun createSearchTracksUseCase(): SearchTracksUseCase {
        val repository = createTrackRepository()
        return SearchTracksUseCase(repository)
    }

    fun createSettingsInteractor(context: Context): SettingsInteractor {
        return SettingsInteractorImpl(SettingsRepositoryImpl(context))
    }
}