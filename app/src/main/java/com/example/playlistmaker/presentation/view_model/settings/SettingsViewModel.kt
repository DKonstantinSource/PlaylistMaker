package com.example.playlistmaker.presentation.view_model.settings


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.domain.settings.theme_preference.api.SettingsInteractor


class SettingsViewModel(private val settingsInteractor: SettingsInteractor) : ViewModel() {

    private val state = MutableLiveData<Boolean>()

    val themePreference: LiveData<Boolean> get() = state

    init {
        state.value = settingsInteractor.getTheme()
    }

    fun setTheme(isChecked: Boolean) {
        settingsInteractor.setTheme(isChecked)
        state.value = isChecked
    }
}