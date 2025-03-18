package com.example.playlistmaker.mapper

import com.example.playlistmaker.data.db.track.TrackEntity
import com.example.playlistmaker.domain.model.Track
import java.util.Date

object TrackMapperEntity {
    fun map(entity: TrackEntity): Track {
        return Track(
            trackId = entity.trackId,
            trackName = entity.trackName,
            artistName = entity.artistName,
            trackTimeMillis = entity.trackTimeMillis,
            artworkUrl100 = entity.artworkUrl100,
            collectionName = entity.collectionName,
            releaseDate = entity.releaseDate?.let { Date(it) },
            primaryGenreName = entity.primaryGenreName,
            country = entity.country,
            previewUrl = entity.previewUrl ?: "null"
        )
    }
}
