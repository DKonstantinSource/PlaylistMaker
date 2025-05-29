package com.example.playlistmaker.presentation.library

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.presentation.search.TrackItem

@Composable
fun FavoriteTracksList(
    tracks: List<Track>,
    onTrackClick: (Track) -> Unit
) {
    val ys_display_medium = TextStyle(
        fontFamily = FontFamily(Font(R.font.ys_display_medium)),
        fontWeight = FontWeight.Medium,
        fontSize = 19.sp
    )


    if (tracks.isEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 106.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.image_no_song),
                contentDescription = null
            )
            Text(
                text = stringResource(R.string.media_library_is_empty),
                fontSize = 19.sp,
                style = ys_display_medium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 16.dp)
            )

        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 16.dp),
            contentPadding = PaddingValues(horizontal = 13.dp)
        ) {
            items(tracks) { track ->
                TrackItem(track = track, onClick = { onTrackClick(track) })
            }
        }
    }
}
