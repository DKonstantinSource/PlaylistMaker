package com.example.playlistmaker.data.settings.theme_settings.repository

import android.content.Context
import com.example.playlistmaker.Constants.THEME_PREFERENCE
import com.example.playlistmaker.domain.settings.theme_preference.api.SettingsInteractor
import com.example.playlistmaker.domain.settings.theme_preference.impl.SettingsInteractorImpl
import com.example.playlistmaker.domain.settings.theme_preference.repository.SettingsRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val themePreferenceModule = module {
    factory<SettingsRepository> { SettingsRepositoryImpl(get()) }
    factory<SettingsInteractor> { SettingsInteractorImpl(get()) }
    single { androidContext().getSharedPreferences(THEME_PREFERENCE, Context.MODE_PRIVATE) }
}