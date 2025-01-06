package com.example.playlistmaker.presentation.ui.library

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityMediaLibraryBinding
import com.example.playlistmaker.presentation.fragments.media_library.FragmentsTitleLibrary
import com.example.playlistmaker.presentation.view_model.library.LibraryViewModel
import com.google.android.material.tabs.TabLayoutMediator

class MediaLibraryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMediaLibraryBinding
    private val viewModel: LibraryViewModel by viewModels<LibraryViewModel>()
    private lateinit var tabMediator: TabLayoutMediator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMediaLibraryBinding.inflate(layoutInflater)
        setContentView(binding.root)


        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, FragmentsTitleLibrary.newInstance())
                .commit()
        }

        viewModel.tracks.observe(this) { tracks ->
            //TODO плюс минус сделаю так если список приъодит пустой, из sh то, заглушку ежеле нет отображаем
            //TODO На макете более 10 трэков, может стать маленькой проблемой !!!!
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        if (::tabMediator.isInitialized) {
            tabMediator.detach()
        }
    }

}