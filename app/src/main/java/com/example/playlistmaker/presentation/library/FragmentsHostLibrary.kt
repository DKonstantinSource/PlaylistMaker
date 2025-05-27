package com.example.playlistmaker.presentation.library

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.*
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.presentation.library.theme.LibraryTheme
import com.example.playlistmaker.presentation.view_model.library.LibraryViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class FragmentsHostLibrary : Fragment() {

    private val libraryViewModel: LibraryViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val navController = findNavController()

        return ComposeView(requireContext()).apply {
            setContent {
                var pendingTrackToPlay by remember { mutableStateOf<Track?>(null) }
                var pendingCreatePlaylist by remember { mutableStateOf(false) }
                var pendingPlaylistToOpen by remember { mutableStateOf<Long?>(null) }

                LaunchedEffect(pendingTrackToPlay) {
                    pendingTrackToPlay?.let { track ->
                        val bundle = Bundle().apply {
                            putParcelable("TRACK_DATA", track)
                            putBoolean("IS_FAVORITE", track.isFavorite)
                        }
                        navController.navigate(
                            R.id.action_libraryTitleFragment_to_fragmentPlayer,
                            bundle
                        )
                        pendingTrackToPlay = null
                    }
                }

                LaunchedEffect(pendingCreatePlaylist) {
                    if (pendingCreatePlaylist) {
                        navController.navigate(R.id.action_libraryTitleFragment_to_fragmentPlayListAdd)
                        pendingCreatePlaylist = false
                    }
                }

                LaunchedEffect(pendingPlaylistToOpen) {
                    pendingPlaylistToOpen?.let { playlistId ->
                        val bundle = Bundle().apply { putLong("playlistId", playlistId) }
                        navController.navigate(
                            R.id.action_libraryTitleFragment_to_fragmentEnterPlaylist,
                            bundle
                        )
                        pendingPlaylistToOpen = null
                    }
                }

                LibraryTheme(darkTheme = isSystemInDarkTheme()) {
                    LibraryScreen(
                        viewModel = libraryViewModel,
                        onTrackClick = { track -> pendingTrackToPlay = track },
                        onCreatePlaylistClicked = { pendingCreatePlaylist = true },
                        onPlaylistClicked = { playlistId -> pendingPlaylistToOpen = playlistId }
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        libraryViewModel.loadPlaylists()
    }
}
