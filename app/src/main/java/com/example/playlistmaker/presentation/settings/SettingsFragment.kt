package com.example.playlistmaker.presentation.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import com.example.playlistmaker.domain.settings.sharing.api.ExternalNavigatorInteractor
import com.example.playlistmaker.presentation.view_model.settings.SettingsViewModel
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : Fragment() {
    private val settingsViewModel: SettingsViewModel by viewModel<SettingsViewModel>()
    private val externalNavigatorInteractor: ExternalNavigatorInteractor by inject()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                SettingsScreen(
                    viewModel = settingsViewModel,
                    onShareClick = { shareNameApp() },
                    onSupportClick = { sendSupportEmail() },
                    onTermsClick = { openTermOfUse() }
                )
            }
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