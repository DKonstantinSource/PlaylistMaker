package com.example.playlistmaker.mapper

import com.example.playlistmaker.data.db.track.TrackEntity
import com.example.playlistmaker.domain.model.Track
import java.util.Date

object FavoriteTrackMapper {
    fun mapToEntity(track: Track): TrackEntity {
        return TrackEntity(
            trackId = track.trackId,
            trackName = track.trackName,
            artistName = track.artistName,
            trackTimeMillis = track.trackTimeMillis,
            artworkUrl100 = track.artworkUrl100,
            collectionName = track.collectionName,
            releaseDate = track.releaseDate?.time,
            primaryGenreName = track.primaryGenreName,
            country = track.country,
            previewUrl = track.previewUrl,
            isFavorit = true,
            timestamp = System.currentTimeMillis()
        )
    }

    fun mapToDomain(entity: TrackEntity): Track {
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
            previewUrl = entity.previewUrl,
            isFavorite = entity.isFavorit
        )
    }
}
