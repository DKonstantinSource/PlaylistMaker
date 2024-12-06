package com.example.playlistmaker.presentation.ui.settings

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import com.example.playlistmaker.Creator.Creator
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.api.SettingsInteractor



class SettingsActivity : AppCompatActivity() {

    private lateinit var settingsInteractor: SettingsInteractor

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        val shareViewImage = findViewById<ImageView>(R.id.shareAppImage)
        val shareViewText = findViewById<TextView>(R.id.shareAppText)

        val textViewEmail= findViewById<TextView>(R.id.supportText)
        val imageViewEmail= findViewById<ImageView>(R.id.supportImage)

        val textTermOfUse = findViewById<TextView>(R.id.termOfUseText)
        val imageTermOfUse = findViewById<ImageView>(R.id.termOfUseImage)

        val backOnMainActivity = findViewById<ImageView>(R.id.backButton)
        backOnMainActivity.setOnClickListener { finish()}

        textTermOfUse.setOnClickListener {openTermOfUse()}
        imageTermOfUse.setOnClickListener {openTermOfUse()}

        shareViewImage.setOnClickListener { shareNameApp() }
        shareViewText.setOnClickListener { shareNameApp() }

        textViewEmail.setOnClickListener { sendSupportEmail() }
        imageViewEmail.setOnClickListener { sendSupportEmail() }

        settingsInteractor = Creator.createSettingsInteractor(this)

        val switchTheme = findViewById<SwitchCompat>(R.id.switchTheme)

        switchTheme.isChecked = settingsInteractor.getTheme()
        switchTheme.setOnCheckedChangeListener { _, isChecked ->
            settingsInteractor.setTheme(isChecked)
            switchTheme(isChecked)
        }



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


    private fun shareNameApp() {
        val linkUrl = getString(R.string.appLinkUrl)
        val intentShare = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, linkUrl)
            type = "text/plain"
        }
        startActivity(Intent.createChooser(intentShare, getString(R.string.shareApp)))
    }




    private fun sendSupportEmail() {

        val emailIntent = Intent(Intent.ACTION_SENDTO)
        emailIntent.setData(Uri.parse("mailto:"))
        emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.emailUrl)))
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.emailSubtext))
        emailIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.emailTitleText))
        startActivity(emailIntent)
    }


    private fun openTermOfUse() {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.termOfUseRl)))
        startActivity(browserIntent)
    }
}