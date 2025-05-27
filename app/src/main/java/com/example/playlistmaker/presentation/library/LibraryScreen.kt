package com.example.playlistmaker.presentation.library

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.presentation.view_model.library.LibraryViewModel

@Composable
fun LibraryScreen(
    viewModel: LibraryViewModel,
    onTrackClick: (Track) -> Unit,
    onCreatePlaylistClicked: () -> Unit,
    onPlaylistClicked: (Long) -> Unit
) {

    val tabs = listOf(stringResource(R.string.favorit_track), stringResource(R.string.play_lists))
    var selectedTabIndex by rememberSaveable { mutableStateOf(0) }

    val favoriteTracks by viewModel.tracks.observeAsState(emptyList())
    val playlists by viewModel.playlists.observeAsState(emptyList())

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = stringResource(R.string.mediatec),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(16.dp)
        )

        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.surface,
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    selectedContentColor = MaterialTheme.colorScheme.surface,
                    unselectedContentColor = MaterialTheme.colorScheme.surface,
                    text = {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                        )
                    }
                )
            }
        }

        when (selectedTabIndex) {
            0 -> FavoriteTracksList(
                tracks = favoriteTracks,
                onTrackClick = onTrackClick
            )

            1 -> PlaylistTabContent(
                playlists = playlists,
                onCreateClick = onCreatePlaylistClicked,
                onPlaylistClick = onPlaylistClicked
            )
        }
    }
}



