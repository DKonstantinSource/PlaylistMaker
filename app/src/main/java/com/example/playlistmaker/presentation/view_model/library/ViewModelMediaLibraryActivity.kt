package com.example.playlistmaker.presentation.view_model.library

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ViewModelMediaLibraryActivity : ViewModel() {
    private val state = MutableLiveData<Unit>()
    val commingSoon: LiveData<Unit> get() = state
}