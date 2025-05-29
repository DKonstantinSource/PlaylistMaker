package com.example.playlistmaker.presentation.search

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.model.Track
import kotlinx.coroutines.delay

@Composable
fun SearchScreen(
    query: String,
    onQueryChange: (String) -> Unit,
    onClearQuery: () -> Unit,
    tracks: List<Track>,
    isHistory: Boolean,
    onClearHistory: () -> Unit,
    onTrackClick: (Track) -> Unit,
    isLoading: Boolean = false,
    isErrorConnection: Boolean = false,
    searchFinished: Boolean = true,
    isNothingFound: Boolean = false,
    navigateToPlayer: (Track) -> Unit,
    onSearch: (String) -> Unit = {}
) {
    var pendingTrackToPlay by remember { mutableStateOf<Track?>(null) }
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(query) {
        if (query.isNotBlank()) {
            delay(2000L)
            onSearch(query)
        }
    }

    val showLoading = isLoading
    val showError = query.isNotBlank() && searchFinished && isErrorConnection
    val showNothingFound =
        query.isNotBlank() && searchFinished && !isErrorConnection && isNothingFound
    val showInitialState = query.isBlank() && tracks.isEmpty()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 12.dp)
    ) {
        Text(
            text = stringResource(R.string.searchText),
            style = MaterialTheme.typography.titleMedium.copy(
                fontSize = 22.sp,
                color = MaterialTheme.colorScheme.onBackground
            ),
            modifier = Modifier
                .padding(top = 10.dp, bottom = 16.dp)
                .align(Alignment.Start)
        )

        CustomSearchBar(
            query = query,
            onQueryChange = onQueryChange,
            onClearQuery = onClearQuery,
            modifier = Modifier
                .fillMaxWidth()
        )


        Spacer(modifier = Modifier.height(16.dp))

        if (isHistory && tracks.isNotEmpty()) {
            Text(
                text = stringResource(R.string.prevSearch),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = 19.sp,
                    color = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 23.dp, bottom = 8.dp)
            )
        }

        when {
            showLoading -> {
                Spacer(modifier = Modifier.height(108.dp))
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }

            showError -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    ErrorPlaceholder(
                        imageRes = R.drawable.image_no_internet,
                        title = stringResource(R.string.errorToConnection),
                        subtitle = stringResource(R.string.unloadFalse),
                        onRetry = { onSearch(query) }
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = { onSearch(query) },
                        modifier = Modifier.size(width = 91.dp, height = 36.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.textOnRefresh),
                            fontSize = 14.sp,
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 1
                        )
                    }
                }
            }

            showNothingFound -> {
                ErrorPlaceholder(
                    imageRes = R.drawable.image_no_song,
                    title = stringResource(R.string.nothingSearch),
                    subtitle = "",
                    onRetry = null
                )
            }

            showInitialState -> {
                // Nothing , but that need.
            }

            else -> {
                val displayedTracks = if (isHistory) {
                    if (tracks.size > 10) tracks.takeLast(10) else tracks
                } else {
                    if (tracks.size > 15) tracks.take(15) else tracks
                }

                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                    ) {
                        itemsIndexed(displayedTracks) { _, track ->
                            TrackItem(track = track) {
                                onTrackClick(track)
                                pendingTrackToPlay = track
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }

        if (isHistory && tracks.isNotEmpty()) {
            Button(
                onClick = onClearHistory,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    text = stringResource(R.string.clearHistory),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.secondary
                    )
                )
            }
        }
    }

    LaunchedEffect(pendingTrackToPlay) {
        pendingTrackToPlay?.let { track ->
            navigateToPlayer(track)
            pendingTrackToPlay = null
        }
    }
}

@Composable
fun ErrorPlaceholder(
    imageRes: Int,
    title: String,
    subtitle: String,
    onRetry: (() -> Unit)?
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 64.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = null,
            modifier = Modifier
                .size(120.dp)
                .padding(bottom = 16.dp)
        )

        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 19.sp,
                color = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier
                .padding(bottom = 8.dp)
                .fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        if (subtitle.isNotEmpty()) {
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 19.sp,
                    color = MaterialTheme.colorScheme.primary
                ),
                color = Color.Gray,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }

        onRetry?.let {
            Button(
                onClick = it,
                modifier = Modifier.padding(top = 24.dp)
            ) {
                Text(stringResource(R.string.textOnRefresh))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SearchScreenPreview() {
    val sampleTracks = listOf(
        Track(
            trackId = 1,
            trackName = "Imagine i Liked",
            artistName = "John Lennon",
            trackTimeMillis = 183000,
            artworkUrl100 = "",
            collectionName = "Imagine",
            releaseDate = null,
            primaryGenreName = "Rock",
            country = "USA",
            previewUrl = null,
            isFavorite = false
        )
    )

    MaterialTheme {
        Surface {
            SearchScreen(
                query = "Imagine",
                onQueryChange = {},
                onClearQuery = {},
                tracks = sampleTracks,
                isHistory = true,
                onClearHistory = {},
                onTrackClick = {},
                onSearch = {},
                navigateToPlayer = {},
            )
        }
    }
}
