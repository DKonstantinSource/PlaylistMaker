package com.example.playlistmaker

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat


class MainActivity : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
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

        sharedPreferences = getSharedPreferences(THEME_PREFERENCE, MODE_PRIVATE)
        val isNightMode = sharedPreferences
            .getBoolean(KEY_SWITCH_THEME, false)
        switchTheme(isNightMode)



    }

    private fun switchTheme(isNightMode: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (isNightMode) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }

    companion object{
        const val KEY_SWITCH_THEME = "is_night_mode"
        const val THEME_PREFERENCE = "theme_preference"
    }
}