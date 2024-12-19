package com.example.playlistmaker.domain.settings.theme_preference.repository

interface SettingsRepository {
    fun getTheme(): Boolean
    fun setTheme(darkTheme: Boolean)
}