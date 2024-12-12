package com.example.playlistmaker

import android.app.Application
import android.content.Intent
import com.example.playlistmaker.Creator.Creator
import com.example.playlistmaker.domain.api.SettingsInteractor
import com.example.playlistmaker.presentation.ui.main.MainActivity

class App : Application() {
    private lateinit var settingsInteractor: SettingsInteractor

    override fun onCreate() {
        super.onCreate()

        settingsInteractor = Creator.createSettingsInteractor(this)
        val themePreference = settingsInteractor.getTheme()

        settingsInteractor.setTheme(themePreference)

        val startActivityMain = Intent(this, MainActivity::class.java)
        startActivityMain.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(startActivityMain)
    }


}