package com.example.playlistmaker.data.db.playlist

import androidx.room.Entity

@Entity(tableName = "playlist_tracks", primaryKeys = ["playlistId", "trackId"])
data class PlaylistTrackCrossRef(
    val playlistId: Long,
    val trackId: Int
)
