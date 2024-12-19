package com.example.playlistmaker.presentation.ui.main

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.playlistmaker.databinding.ActivityMainBinding
import com.example.playlistmaker.presentation.ui.search.SearchActivity
import com.example.playlistmaker.presentation.ui.library.MediaLibraryActivity
import com.example.playlistmaker.presentation.ui.settings.SettingsActivity
import com.example.playlistmaker.presentation.view_model.main.MainViewModel


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupEdgeToEdge()

        binding.search.setOnClickListener {
            viewModel.onSearchClicked()
        }
        binding.library.setOnClickListener {
            viewModel.onLibraryClicked()
        }
        binding.settings.setOnClickListener {
            viewModel.onSettingsClicked()
        }

        viewModel.navigateTo.observe(this) { navigationTarget ->
            navigationTarget?.let {
                when (it) {
                    MainViewModel.NavigationTarget.SEARCH -> {
                        startActivity(Intent(this, SearchActivity::class.java))
                        viewModel.navigationDone()
                    }

                    MainViewModel.NavigationTarget.LIBRARY -> {
                        startActivity(Intent(this, MediaLibraryActivity::class.java))
                        viewModel.navigationDone()
                    }

                    MainViewModel.NavigationTarget.SETTINGS -> {
                        startActivity(Intent(this, SettingsActivity::class.java))
                        viewModel.navigationDone()
                    }
                }
            }
        }


    }

    private fun setupEdgeToEdge() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}