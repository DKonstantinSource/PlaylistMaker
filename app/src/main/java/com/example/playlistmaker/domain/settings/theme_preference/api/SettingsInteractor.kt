package com.example.playlistmaker.domain.settings.theme_preference.api

interface SettingsInteractor {
    fun getTheme(): Boolean
    fun setTheme(darkTheme: Boolean)
}