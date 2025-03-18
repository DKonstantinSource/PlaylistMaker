package com.example.playlistmaker.data.impl

import com.example.playlistmaker.data.db.track.TrackDao
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.domain.repository.FavoriteTracksRepository
import com.example.playlistmaker.mapper.FavoriteTrackMapper.mapToDomain
import com.example.playlistmaker.mapper.FavoriteTrackMapper.mapToEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavoriteTracksRepositoryImpl(private val trackDao: TrackDao) : FavoriteTracksRepository {

    override suspend fun addTrackToFavorites(track: Track) {
        val trackEntity = mapToEntity(track)
        trackDao.insertTrack(trackEntity)
    }

    override suspend fun removeTrackFromFavorites(track: Track) {
        val trackEntity = mapToEntity(track)
        trackDao.deleteTrack(trackEntity)
    }

    override fun getFavoriteTracks(): Flow<List<Track>> {
        return trackDao.getTracks()
            .map { trackEntities -> trackEntities.map { mapToDomain(it) } }
    }

    override fun getFavoriteTrackIds(): Flow<List<Int>> {
        return trackDao.getTrackIds()
    }
}


