package com.example.playlistmaker.data.db.playlist

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlist_table")
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true) val playlistId: Long = 0,
    val name: String,
    val description: String? = "",
    val imagePath: String? = "",
    val trackCount: Int = 0,
    val tracks: String,
    val createdAt: Long = System.currentTimeMillis()
)
