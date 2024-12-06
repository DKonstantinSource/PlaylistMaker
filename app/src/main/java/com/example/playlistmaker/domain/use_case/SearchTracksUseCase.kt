package com.example.playlistmaker.domain.use_case

import android.os.Handler
import android.os.Looper
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.domain.repository.TrackRepository
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class SearchTracksUseCase(private val repository: TrackRepository) {
    private val executor: ExecutorService = Executors.newCachedThreadPool()

    fun execute(term: String, callback: (List<Track>?) -> Unit) {
        executor.submit {
            val tracks = repository.searchTracks(term)
            Handler(Looper.getMainLooper()).post {
                callback(tracks)
            }
        }
    }

    fun shutdown() {
        executor.shutdown()
    }
}