package com.example.playlistmaker.presentation.search

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.playlistmaker.domain.model.Track
import java.text.SimpleDateFormat

@Composable
fun TrackList(tracks: List<Track>, onTrackClick: (Track) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        items(tracks) { track ->
            Log.e("Track in list Click", "Track list done")
            TrackItem(track = track, onClick = { onTrackClick(track) })
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TrackListPreview() {
    val date = SimpleDateFormat("yyyy-MM-dd").parse("1971-10-11")
    val tracks = listOf(
        Track(
            trackId = 1L,
            trackName = "Imagine",
            artistName = "John Lennon",
            trackTimeMillis = 195000,
            artworkUrl100 = "https://upload.wikimedia.org/wikipedia/en/5/5e/ImagineCover.jpg",
            collectionName = "Imagine",
            releaseDate = date,
            primaryGenreName = "Rock",
            country = "USA",
            previewUrl = "https://example.com/preview1.mp3",
            isFavorite = false
        ),
        Track(
            trackId = 2L,
            trackName = "Bohemian Rhapsody",
            artistName = "Queen",
            trackTimeMillis = 355000,
            artworkUrl100 = "https://upload.wikimedia.org/wikipedia/en/9/9f/Bohemian_Rhapsody.png",
            collectionName = "A Night at the Opera",
            releaseDate = date,
            primaryGenreName = "Rock",
            country = "UK",
            previewUrl = "https://example.com/preview2.mp3",
            isFavorite = true
        )
    )

    MaterialTheme {
        Surface {
            TrackList(tracks = tracks, onTrackClick = {})
        }
    }
}
