package com.example.playlistmaker.presentation.search


import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.playlistmaker.domain.model.Track
import java.text.SimpleDateFormat

@Composable
fun TrackItem(track: Track, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(1.dp, 4.dp, 8.dp, 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = rememberAsyncImagePainter(track.artworkUrl100),
            contentDescription = "Track artwork",
            modifier = Modifier
                .size(45.dp)
                .clip(RoundedCornerShape(2.dp)),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(8.dp))

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(0.dp, 0.dp, 0.dp, 8.dp)
        ) {
            Text(
                text = track.trackName,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurface
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )


            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = track.artistName,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.surface
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.width(4.dp))

                Text(
                    text = "•",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.surface
                    )
                )

                Spacer(modifier = Modifier.width(4.dp))

                Text(
                    text = track.trackTimeMillis.toFormattedTime(),
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.surface
                    )
                )
            }


        }

        Icon(
            imageVector = Icons.Default.KeyboardArrowRight,
            contentDescription = "Go to details, fk yeah it's work",
            tint = MaterialTheme.colorScheme.surface
        )
    }
}

fun Int.toFormattedTime(): String {
    val minutes = this / 60000
    val seconds = (this % 60000) / 1000
    return "%d:%02d".format(minutes, seconds)
}


@Preview(showBackground = true)
@Composable
fun TrackItemPreview() {
    val sampleTrack = Track(
        trackId = 1L,
        trackName = "Imagine",
        artistName = "John Lennon",
        trackTimeMillis = 195000,
        artworkUrl100 = "https://upload.wikimedia.org/wikipedia/en/5/5e/ImagineCover.jpg",
        collectionName = "Imagine",
        releaseDate = SimpleDateFormat("yyyy-MM-dd").parse("1971-10-11"),
        primaryGenreName = "Rock",
        country = "USA",
        previewUrl = "https://example.com/preview.mp3",
        isFavorite = false
    )

    MaterialTheme {
        Surface {
            TrackItem(track = sampleTrack, onClick = {})
        }
    }
}
