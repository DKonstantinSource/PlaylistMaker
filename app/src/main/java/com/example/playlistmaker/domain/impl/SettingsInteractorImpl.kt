package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.data.repository.SettingsRepository
import com.example.playlistmaker.domain.api.SettingsInteractor

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