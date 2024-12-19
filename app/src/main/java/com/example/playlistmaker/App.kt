package com.example.playlistmaker

import android.app.Application
import android.content.Intent
import com.example.playlistmaker.Creator.Creator
import com.example.playlistmaker.domain.settings.theme_preference.api.SettingsInteractor
import com.example.playlistmaker.presentation.ui.main.MainActivity

class App : Application() {
    private lateinit var settingsInteractor: SettingsInteractor

    override fun onCreate() {
        super.onCreate()
        Creator.initApplication(this)
        settingsInteractor = Creator.createSettingsInteractor()
        settingsInteractor.setTheme(settingsInteractor.getTheme())

        startActivity(Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        })
    }


}