package com.example.playlistmaker.data.impl

import android.util.Log
import com.example.playlistmaker.data.db.TrackDao
import com.example.playlistmaker.data.db.TrackEntity
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.domain.repository.FavoriteTracksRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

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
    isFavorite = true // указываем, что этот трек является любимым
)

fun TrackEntity.toDomain() = Track(
    trackId = trackId,
    trackName = trackName,
    artistName = artistName,
    trackTimeMillis = trackTimeMillis,
    artworkUrl100 = artworkUrl100,
    collectionName = collectionName,
    releaseDate = releaseDate?.let { java.util.Date(it) },
    primaryGenreName = primaryGenreName,
    country = country,
    previewUrl = previewUrl
)

class FavoriteTracksRepositoryImpl(private val trackDao: TrackDao) : FavoriteTracksRepository {
    override suspend fun addTrackToFavorites(track: Track) {
        Log.d("AddTrack", "Track add on DB")
        trackDao.insertTrack(track.toEntity())
    }

    override suspend fun removeTrackFromFavorites(track: Track) {
        trackDao.deleteTrack(track.toEntity())
    }

    override fun getFavoriteTracks(): Flow<List<Track>> {
        Log.d("RepositoryGet", "data all track ")
        return trackDao.getAllTracks()
            .map { trackEntities ->
                if (trackEntities.isEmpty()) {
                    emptyList<Track>()
                } else {
                    trackEntities.map { it.toDomain() }
                }
            }
    }


    override fun getFavoriteTrackIds(): Flow<List<Int>> {
        Log.d("RepositoryGet", "data get favorite ")
        return trackDao.getFavoriteTrackIds()
    }
}
