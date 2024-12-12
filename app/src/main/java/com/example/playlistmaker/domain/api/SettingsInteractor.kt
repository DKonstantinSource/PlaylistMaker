package com.example.playlistmaker.domain.api

interface SettingsInteractor {
    fun getTheme(): Boolean
    fun setTheme(darkTheme: Boolean)
}