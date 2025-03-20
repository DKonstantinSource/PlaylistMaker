package com.example.playlistmaker.data.db.playlist.track_add_playlist

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlist_track_entity")
data class PlaylistTrackEntity(
    @PrimaryKey
    @ColumnInfo(name = "track_id")
    val trackId: Long,

    @ColumnInfo(name = "track_name")
    val trackName: String,

    @ColumnInfo(name = "artist_name")
    val artistName: String,

    @ColumnInfo(name = "track_time_millis")
    val trackTimeMillis: Int,

    @ColumnInfo(name = "artwork_url_100")
    val artworkUrl100: String,

    @ColumnInfo(name = "collection_name")
    val collectionName: String,

    @ColumnInfo(name = "release_date")
    val releaseDate: Long?,

    @ColumnInfo(name = "primary_genre_name")
    val primaryGenreName: String,

    @ColumnInfo(name = "country")
    val country: String,

    @ColumnInfo(name = "preview_url")
    val previewUrl: String?,

    @ColumnInfo(name = "is_favorite")
    val isFavorite: Boolean,

    @ColumnInfo(name = "added_date")
    val addedDate: Long = System.currentTimeMillis()
)
