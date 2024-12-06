package com.example.playlistmaker.data.impl

import android.content.Context
import com.example.playlistmaker.Constants.KEY_SWITCH_THEME
import com.example.playlistmaker.Constants.THEME_PREFERENCE
import com.example.playlistmaker.data.repository.SettingsRepository

class SettingsRepositoryImpl(context: Context) : SettingsRepository {

    private val sharedPreferences =
        context.getSharedPreferences(THEME_PREFERENCE, Context.MODE_PRIVATE)

    @Override
    override fun getTheme(): Boolean {
        return sharedPreferences.getBoolean(KEY_SWITCH_THEME, false)
    }


    @Override
    override fun setTheme(darkTheme: Boolean) {

        sharedPreferences.edit()
            .putBoolean(KEY_SWITCH_THEME, darkTheme)
            .apply()
    }

}