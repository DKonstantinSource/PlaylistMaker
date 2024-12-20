package com.example.playlistmaker.presentation.view_model.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {
    private val navigationTo = MutableLiveData<NavigationTarget?>()
    val navigateTo: LiveData<NavigationTarget?> get() = navigationTo

    fun onSearchClicked() {
        navigationTo.value = NavigationTarget.SEARCH
    }

    fun onLibraryClicked() {
        navigationTo.value = NavigationTarget.LIBRARY
    }

    fun onSettingsClicked() {
        navigationTo.value = NavigationTarget.SETTINGS
    }

    fun navigationDone() {
        navigationTo.value = null
    }

    sealed class NavigationTarget {
        object SEARCH : NavigationTarget()
        object LIBRARY : NavigationTarget()
        object SETTINGS : NavigationTarget()
    }
}