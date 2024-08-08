package com.example.playlistmaker

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        val searchView = findViewById<Button>(R.id.first_button)
        val mediaLibraryView = findViewById<Button>(R.id.second_button)
        val settingView = findViewById<Button>(R.id.third_button)

        val searchViewDark = findViewById<Button>(R.id.first_button_dark)
        val mediaLibraryViewDark = findViewById<Button>(R.id.second_button_dark)
        val settingViewDark = findViewById<Button>(R.id.third_button_dark)

        searchView.setOnClickListener {
            val displaySettingsActivity = Intent(this, SearchActivity::class.java)
            startActivity(displaySettingsActivity)
        }
        mediaLibraryView.setOnClickListener {
            val displaySettingsActivity = Intent(this, MediaLibraryActivity::class.java)
            startActivity(displaySettingsActivity)
        }
        settingView.setOnClickListener {
            val displaySettingsActivity = Intent(this, SettingsActivity::class.java)
            startActivity(displaySettingsActivity)
        }


    }
}





//ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//insets

//}
