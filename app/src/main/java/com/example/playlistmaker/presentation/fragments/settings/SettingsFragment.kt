package com.example.playlistmaker.presentation.fragments.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SwitchCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.playlistmaker.databinding.FragmentSettingsBinding
import com.example.playlistmaker.domain.settings.sharing.api.ExternalNavigatorInteractor
import com.example.playlistmaker.presentation.view_model.settings.SettingsViewModel
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private val settingsViewModel: SettingsViewModel by viewModel<SettingsViewModel>()
    private val externalNavigatorInteractor: ExternalNavigatorInteractor by inject()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        setupEdgeToEdge()
        setupClickListeners()
        setupThemeSwitch()
        return binding.root
    }

    private fun setupEdgeToEdge() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.settingsFragmentScreen) { v, insets ->
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
    }

    private fun setupThemeSwitch() {
        val switchTheme: SwitchCompat = binding.switchTheme

        settingsViewModel.themePreference.observe(viewLifecycleOwner) { isChecked ->
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

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}