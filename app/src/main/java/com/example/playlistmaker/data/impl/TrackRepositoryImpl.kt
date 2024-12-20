package com.example.playlistmaker.data.impl

import android.util.Log
import com.example.playlistmaker.mapper.TrackMapper
import com.example.playlistmaker.data.network.API.ApiService
import com.example.playlistmaker.data.network.response.SearchResponse
import com.example.playlistmaker.domain.repository.TrackRepository
import retrofit2.Response
import com.example.playlistmaker.domain.model.Track as DomainTrack

class TrackRepositoryImpl(private val apiService: ApiService) : TrackRepository {
    override fun searchTracks(term: String): List<DomainTrack>? {
        return try {
            val response: Response<SearchResponse> = apiService.searchTracks(term).execute()

            if (response.isSuccessful) {
                response.body()?.results?.map { TrackMapper.map(it) }
            } else {
                Log.e("Boje Pravi Esli Vidish 200 POMOLIS ", "Error: ${response.code()}")
                null
            }
        } catch (e: Exception) {
            Log.e("TrackRepositoryImpl", "Exception occurred", e)
            null
        }
    }
}