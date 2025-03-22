package com.example.playlistmaker.domain.settings.sharing.impl

import com.example.playlistmaker.domain.settings.sharing.api.ExternalNavigatorInteractor
import com.example.playlistmaker.domain.settings.sharing.repository.ExternalNavigator


class ExternalNavigatorInteractorImpl(
    private val externalNavigator: ExternalNavigator
) : ExternalNavigatorInteractor {

    override fun shareApp() {
        externalNavigator.shareApp()
    }

    override fun openTermsOfUse() {
        externalNavigator.openTermsOfUse()
    }

    override fun openSupport() {
        externalNavigator.openSupport()
    }

    override fun sharePlaylistApp(string: String) {
        externalNavigator.sharePlaylistApp(string)
    }
}