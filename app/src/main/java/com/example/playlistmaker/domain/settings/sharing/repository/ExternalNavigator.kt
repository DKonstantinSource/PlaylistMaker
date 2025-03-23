package com.example.playlistmaker.domain.settings.sharing.repository


interface ExternalNavigator {
    fun shareApp()
    fun openSupport()
    fun openTermsOfUse()
    fun sharePlaylistApp(string: String)
}