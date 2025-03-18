package com.example.playlistmaker.data.impl

import com.example.playlistmaker.data.db.track.TrackDao
import com.example.playlistmaker.data.db.track.TrackEntity
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.domain.repository.FavoriteTracksRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Date

fun Track.toEntity() = TrackEntity(
    trackId = trackId,
    trackName = trackName,
    artistName = artistName,
    trackTimeMillis = trackTimeMillis,
    artworkUrl100 = artworkUrl100,
    collectionName = collectionName,
    releaseDate = releaseDate?.time,
    primaryGenreName = primaryGenreName,
    country = country,
    previewUrl = previewUrl,
    isFavorit = isFavorite,
    timestamp = System.currentTimeMillis()
)

fun TrackEntity.toDomain() = Track(
    trackId = trackId,
    trackName = trackName,
    artistName = artistName,
    trackTimeMillis = trackTimeMillis,
    artworkUrl100 = artworkUrl100,
    collectionName = collectionName,
    releaseDate = releaseDate?.let { Date(it) },
    primaryGenreName = primaryGenreName,
    country = country,
    previewUrl = previewUrl,
    isFavorite = isFavorit,

)

class FavoriteTracksRepositoryImpl(private val trackDao: TrackDao) : FavoriteTracksRepository {

    override suspend fun addTrackToFavorites(track: Track) {
        val trackEntity = track.copy(isFavorite = true).toEntity()
        trackDao.insertTrack(trackEntity)
    }

    override suspend fun removeTrackFromFavorites(track: Track) {
        val trackEntity = track.copy(isFavorite = false).toEntity()
        trackDao.deleteTrack(trackEntity)
    }

    override fun getFavoriteTracks(): Flow<List<Track>> {
        return trackDao.getTracks()
            .map { trackEntities ->
                trackEntities.map { it.toDomain() }
            }
    }

    override fun getFavoriteTrackIds(): Flow<List<Int>> {
        return trackDao.getTrackIds()
    }
}
