package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.domain.repository.TrackRepository
import kotlinx.coroutines.flow.Flow


class SearchTracksInteractorImpl(private val repository: TrackRepository) {
    fun execute(term: String): Flow<List<Track>> = repository.searchTracks(term)
}