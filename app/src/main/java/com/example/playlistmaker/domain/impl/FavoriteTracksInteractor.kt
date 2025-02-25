package com.example.playlistmaker.domain.impl

import android.util.Log
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.domain.repository.FavoriteTracksRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavoriteTracksInteractor(private val repository: FavoriteTracksRepository) {
    suspend fun addTrack(track: Track) {
        repository.addTrackToFavorites(track)
    }

    suspend fun removeTrack(track: Track) {
        repository.removeTrackFromFavorites(track)
    }

    fun getFavoriteTracks(): Flow<List<Track>> {
        Log.d("RepositoryGet", "domain ")
        return repository.getFavoriteTracks().map { tracks ->
            tracks.sortedByDescending { it.trackId }
        }
    }

    fun getFavoriteTrackIds(): Flow<List<Int>> {
        return repository.getFavoriteTrackIds()
    }
}
