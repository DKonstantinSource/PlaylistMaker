package com.example.playlistmaker.data.db.playlist.track_add_playlist

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlist_track_entity")
data class PlaylistTrackEntity(
    @PrimaryKey val trackId: Long,
    val trackName: String,
    val artistName: String,
    val previewUrl: String?
)
