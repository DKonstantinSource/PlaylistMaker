package com.example.playlistmaker.presentation.view_model.settings


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.domain.settings.theme_preference.api.SettingsInteractor


class SettingsViewModelFactory(
    private val settingsInteractor: SettingsInteractor

) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {


            @Suppress("UNCHECKED_CAST")
            return SettingsViewModel(settingsInteractor) as T


        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}