package com.example.playlistmaker.presentation.ui.settings

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.appcompat.widget.SwitchCompat
import com.example.playlistmaker.Creator.Creator
import com.example.playlistmaker.databinding.ActivitySettingsBinding
import com.example.playlistmaker.domain.settings.sharing.api.ExternalNavigatorInteractor
import com.example.playlistmaker.presentation.view_model.settings.SettingsViewModel
import com.example.playlistmaker.presentation.view_model.settings.SettingsViewModelFactory


class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
    private val settingsViewModel: SettingsViewModel by viewModels {
        SettingsViewModelFactory(
            Creator.createSettingsInteractor()
        )
    }
    private lateinit var externalNavigatorInteractor: ExternalNavigatorInteractor
    private lateinit var viewModel: SettingsViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        externalNavigatorInteractor = Creator.createExternalNavigatorInteractor()
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = settingsViewModel

        enableEdgeToEdge()
        setupEdgeToEdge()
        setupClickListeners()
        setupThemeSwitch()
    }

    private fun setupEdgeToEdge() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupClickListeners() {
        binding.shareAppImage.setOnClickListener { shareNameApp() }
        binding.shareAppText.setOnClickListener { shareNameApp() }
        binding.supportText.setOnClickListener { sendSupportEmail() }
        binding.supportImage.setOnClickListener { sendSupportEmail() }
        binding.termOfUseText.setOnClickListener { openTermOfUse() }
        binding.termOfUseImage.setOnClickListener { openTermOfUse() }
        binding.backButton.setOnClickListener { finish() }
    }

    private fun setupThemeSwitch() {
        val switchTheme: SwitchCompat = binding.switchTheme

        settingsViewModel.themePreference.observe(this) { isChecked ->
            switchTheme.isChecked = isChecked
        }

        switchTheme.setOnCheckedChangeListener { _, isChecked ->
            settingsViewModel.setTheme(isChecked)
        }
    }

    private fun shareNameApp() {
        externalNavigatorInteractor.shareApp()
    }

    private fun sendSupportEmail() {
        externalNavigatorInteractor.openSupport()
    }

    private fun openTermOfUse() {
        externalNavigatorInteractor.openTermsOfUse()
    }
}