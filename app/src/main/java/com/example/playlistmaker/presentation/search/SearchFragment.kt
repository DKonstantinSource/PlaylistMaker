package com.example.playlistmaker.presentation.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.Constants.IS_FAVORITE
import com.example.playlistmaker.R
import com.example.playlistmaker.presentation.fragments.player.FragmentPlayer
import com.example.playlistmaker.presentation.view_model.search.SearchViewModel
import org.koin.androidx.viewmodel.ext.android.stateViewModel

class SearchFragment : Fragment() {

    private val viewModel: SearchViewModel by stateViewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {

                val searchStatus by viewModel.searchStatus.observeAsState(SearchStatus.Idle)
                val tracks by viewModel.tracks.observeAsState(emptyList())
                val query by viewModel.query.observeAsState("")
                val isLoading by viewModel.isLoading.observeAsState(false)

                val showLoading = searchStatus == SearchStatus.Loading
                val showError = searchStatus == SearchStatus.Error
                val showNothingFound = searchStatus == SearchStatus.Success && tracks.isEmpty()

                SearchTheme(darkTheme = isSystemInDarkTheme()) {

                    SearchScreen(
                        query = query,
                        onQueryChange = { viewModel.onSearchQueryChanged(it) },
                        onClearQuery = { viewModel.onSearchQueryChanged("") },
                        onSearch = { viewModel.searchTracks(it) },
                        tracks = tracks,
                        isHistory = (searchStatus == SearchStatus.Idle) && !isLoading && tracks.isNotEmpty(),
                        onClearHistory = { viewModel.clearHistory() },
                        isLoading = showLoading,
                        isErrorConnection = showError,
                        isNothingFound = showNothingFound,
                        onTrackClick = { track -> viewModel.trackClicked(track) },
                        navigateToPlayer = { track ->
                            val bundle = Bundle().apply {
                                putParcelable(FragmentPlayer.TRACK_DATA, track)
                                putBoolean(IS_FAVORITE, track.isFavorite)
                            }
                            findNavController().navigate(
                                R.id.action_searchFragment_to_fragmentPlayer,
                                bundle
                            )
                        },
                        searchFinished = searchStatus == SearchStatus.Success || searchStatus == SearchStatus.Error
                    )

                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.updateTracks()
    }

    override fun onStop() {
        super.onStop()
        viewModel.clearDateTrack()
    }
}
