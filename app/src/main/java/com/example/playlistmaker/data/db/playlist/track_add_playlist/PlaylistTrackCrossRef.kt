package com.example.playlistmaker.data.db.playlist.track_add_playlist

import androidx.room.Entity

@Entity(tableName = "playlist_tracks_cross", primaryKeys = ["playlistId", "trackId"])
data class PlaylistTrackCrossRef(
    val playlistId: Long,
    val trackId: Long
)

