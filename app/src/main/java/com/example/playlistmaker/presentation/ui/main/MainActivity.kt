package com.example.playlistmaker.presentation.ui.main

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.playlistmaker.R
import com.example.playlistmaker.presentation.ui.search.SearchActivity
import com.example.playlistmaker.presentation.ui.library.MediaLibraryActivity
import com.example.playlistmaker.presentation.ui.settings.SettingsActivity


class MainActivity : AppCompatActivity() {
    private lateinit var searchView: Button
    private lateinit var mediaLibraryView: Button
    private lateinit var settingView: Button
    private lateinit var displaySettingsActivity: Intent

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        searchView = findViewById(R.id.search)
        mediaLibraryView = findViewById(R.id.library)
        settingView = findViewById(R.id.settings)

        searchView.setOnClickListener {
            displaySettingsActivity = Intent(this, SearchActivity::class.java)
            startActivity(displaySettingsActivity)
        }
        mediaLibraryView.setOnClickListener {
            displaySettingsActivity = Intent(this, MediaLibraryActivity::class.java)
            startActivity(displaySettingsActivity)
        }
        settingView.setOnClickListener {
            displaySettingsActivity = Intent(this, SettingsActivity::class.java)
            startActivity(displaySettingsActivity)
        }


    }

}