package com.example.playlistmaker.presentation.library


import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.domain.model.Playlist
import java.util.*

@Preview(showBackground = true)
@Composable
fun LibraryScreenPreview() {
    val sampleTracks = listOf(
        Track(
            trackId = 1,
            trackName = "Imagine YEAH",
            artistName = "John Lennon",
            trackTimeMillis = 183000,
            artworkUrl100 = "https://is5-ssl.mzstatic.com/image/thumb/Music115/v4/f2/5d/ea/f25deaa7-3e15-5a9c-6f0f-b1eb33fc539b/075679823645.jpg/100x100bb.jpg",
            collectionName = "Imagine FUCK YEAH",
            releaseDate = Date(),
            primaryGenreName = "Rock",
            country = "USA",
            previewUrl = null,
            isFavorite = true
        ),
        Track(
            trackId = 2,
            trackName = "Bohemian Rhapsody",
            artistName = "Queen",
            trackTimeMillis = 355000,
            artworkUrl100 = "https://is1-ssl.mzstatic.com/image/thumb/Music128/v4/83/30/2c/83302ccf-9059-f9db-fd88-e09a0e0e51d7/00042282561308.rgb.jpg/100x100bb.jpg",
            collectionName = "A Night at the Opera",
            releaseDate = Date(),
            primaryGenreName = "Rock",
            country = "UK",
            previewUrl = null,
            isFavorite = true
        )
    )

    val samplePlaylists = listOf(
        Playlist(
            id = 1,
            name = "Rock Classics",
            description = "Лучшие рок-композиции",
            imagePath = null,
            trackCount = 2,
            tracks = sampleTracks
        ),
        Playlist(
            id = 2,
            name = "Chill Vibes",
            description = "Спокойные треки для отдыха",
            imagePath = null,
            trackCount = 1,
            tracks = listOf(sampleTracks[0])
        )
    )

    var selectedTabIndex by remember { mutableStateOf(1) }

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = stringResource(R.string.mediatec),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(16.dp)
        )

        val tabs =
            listOf(stringResource(R.string.favorit_track), stringResource(R.string.play_lists))

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
                tracks = sampleTracks,
                onTrackClick = {}
            )

            1 -> PlaylistTabContent(
                playlists = samplePlaylists,
                onCreateClick = {},
                onPlaylistClick = {}
            )
        }
    }
}

