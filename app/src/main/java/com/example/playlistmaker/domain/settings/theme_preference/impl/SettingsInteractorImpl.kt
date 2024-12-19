package com.example.playlistmaker.domain.settings.theme_preference.impl

import com.example.playlistmaker.domain.settings.theme_preference.repository.SettingsRepository
import com.example.playlistmaker.domain.settings.theme_preference.api.SettingsInteractor

class SettingsInteractorImpl(private val settingsRepository: SettingsRepository) :
    SettingsInteractor {
    @Override
    override fun getTheme(): Boolean {
        return settingsRepository.getTheme()
    }

    @Override
    override fun setTheme(darkTheme: Boolean) {
        settingsRepository.setTheme(darkTheme)
    }
}