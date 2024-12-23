package com.example.playlistmaker.data.settings.theme_settings.repository
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.Constants.KEY_SWITCH_THEME
import com.example.playlistmaker.domain.settings.theme_preference.repository.SettingsRepository


class SettingsRepositoryImpl(
    private val sharedPreferences: SharedPreferences
) : SettingsRepository {

    @Override
    override fun getTheme(): Boolean {

        val isSystemDarkMode =
            AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES

        if (isSystemDarkMode) {
            return true
        }

        return if (sharedPreferences.contains(KEY_SWITCH_THEME)) {
            sharedPreferences.getBoolean(KEY_SWITCH_THEME, false)
        } else {
            false
        }
    }

    @Override
    override fun setTheme(darkTheme: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (darkTheme) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )

        sharedPreferences.edit()
            .putBoolean(KEY_SWITCH_THEME, darkTheme)
            .apply()
    }
}