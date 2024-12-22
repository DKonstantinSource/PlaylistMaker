package com.example.playlistmaker

import android.app.Application
import android.content.Intent
import com.example.playlistmaker.di.dataModule
import com.example.playlistmaker.domain.settings.theme_preference.api.SettingsInteractor
import com.example.playlistmaker.presentation.ui.main.MainActivity
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class App : Application() {

    private val settingsInteractor: SettingsInteractor by inject()
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@App)
            modules(dataModule)
        }

        settingsInteractor.setTheme(settingsInteractor.getTheme())

        startActivity(Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        })
    }
}