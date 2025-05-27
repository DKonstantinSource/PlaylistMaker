package com.example.playlistmaker

import android.app.Application
import android.content.Intent
import com.example.playlistmaker.data.db.repositoryModule
import com.example.playlistmaker.data.historyListModule
import com.example.playlistmaker.data.network.networkModule
import com.example.playlistmaker.data.player.mediaPlayerModule
import com.example.playlistmaker.data.settings.sharing.imp.externalNavigationModule
import com.example.playlistmaker.data.settings.theme_settings.repository.themePreferenceModule
import com.example.playlistmaker.di.dataModule
import com.example.playlistmaker.domain.settings.theme_preference.api.SettingsInteractor
import com.example.playlistmaker.presentation.ui.host.HostActivity
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class App : Application() {

    private val settingsInteractor: SettingsInteractor by inject()
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            modules(
                listOf(
                    dataModule, mediaPlayerModule, networkModule, themePreferenceModule,
                    externalNavigationModule, historyListModule, repositoryModule,
                )
            )
        }
        settingsInteractor.setTheme(settingsInteractor.getTheme())
    }

}