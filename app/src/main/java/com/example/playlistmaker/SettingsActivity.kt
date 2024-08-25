package com.example.playlistmaker

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.Switch
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import com.example.playlistmaker.R.string.termsOfUse
import java.util.concurrent.atomic.AtomicBoolean

class SettingsActivity : AppCompatActivity() {

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

        val isLoading = AtomicBoolean()
        if (isLoading.compareAndSet(false, true)) {
            // todo
        } else {
            isLoading.set(false)
        }

        val switchTheme = findViewById<SwitchCompat>(R.id.switchDarkTheme)
        switchTheme.isChecked = when (resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)) {
            Configuration.UI_MODE_NIGHT_YES -> true
            else -> false
        }

        switchTheme.setOnCheckedChangeListener { buttonView, isChecked ->
            AppCompatDelegate.setDefaultNightMode(
                if(isChecked) {
                    AppCompatDelegate.MODE_NIGHT_YES
                } else {
                    AppCompatDelegate.MODE_NIGHT_NO
                }
            )
        }









    }

    private fun shareNameApp() {
        val linkUrl = "https://practicum.yandex.ru/android-developer/?from=catalog"
        val intentShare = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, linkUrl)
            type = "text/plain"
        }
        val chooser = Intent.createChooser(intentShare, "Поделиться приложением через")
        startActivity(chooser)
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