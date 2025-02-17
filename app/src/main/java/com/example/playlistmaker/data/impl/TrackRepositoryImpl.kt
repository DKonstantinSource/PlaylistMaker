package com.example.playlistmaker.data.impl


import com.example.playlistmaker.mapper.TrackMapper
import com.example.playlistmaker.data.network.API.ApiService
import com.example.playlistmaker.domain.repository.TrackRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import com.example.playlistmaker.domain.model.Track as DomainTrack

class TrackRepositoryImpl(private val apiService: ApiService) : TrackRepository {
    override fun searchTracks(term: String): Flow<List<DomainTrack>> = flow {
        try {
            val response = apiService.searchTracks(term)
            emit(response.results.map { TrackMapper.map(it) })
        } catch (e: Exception) {
            emit(emptyList())
        }
    }
}