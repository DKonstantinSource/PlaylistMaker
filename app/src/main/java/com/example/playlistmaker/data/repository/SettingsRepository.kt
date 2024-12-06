package com.example.playlistmaker.data.repository

interface SettingsRepository {
    fun getTheme(): Boolean
    fun setTheme(darkTheme: Boolean)
}