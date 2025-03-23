package com.example.playlistmaker.data.impl

import android.util.Log
import com.example.playlistmaker.data.db.AppDatabase
import com.example.playlistmaker.data.db.playlist.track_add_playlist.PlaylistTrackCrossRef
import com.example.playlistmaker.data.db.playlist.track_add_playlist.PlaylistTrackDao
import com.example.playlistmaker.data.db.playlist.track_add_playlist.PlaylistTrackEntity
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.domain.repository.TrackAddToPlaylistRepository
import java.util.Date

class TrackAddToPlaylistRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val playlistTrackDao: PlaylistTrackDao
) : TrackAddToPlaylistRepository {

    override suspend fun addTrackToPlaylist(track: Track, playlistId: Long) {
        val crossRef = PlaylistTrackCrossRef(
            playlistId = playlistId,
            trackId = track.trackId
        )
        appDatabase.playlistTrackCrossRefDao().insertCrossRef(crossRef)

        val trackEntity = PlaylistTrackEntity(
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
            isFavorite = track.isFavorite,
        )
        playlistTrackDao.insertTrack(trackEntity)
    }


    override suspend fun removeTrackFromPlaylist(playlistId: Long, trackId: Long) {
        appDatabase.playlistTrackCrossRefDao().deleteTrackFromPlaylist(playlistId, trackId)
        playlistTrackDao.removeTrackFromPlaylist(playlistId, trackId)
    }

    override suspend fun isTrackInPlaylist(playlistId: Long, trackId: Long): Boolean {
        return appDatabase.playlistTrackCrossRefDao().isTrackInPlaylist(playlistId, trackId)
    }

    override suspend fun getCurrentlyCountTrack(playlistId: Long): Int {
        return appDatabase.playlistTrackCrossRefDao().getTrackCountInPlaylist(playlistId)
    }

    override suspend fun getTracksForPlaylist(playlistId: Long): List<Track> {
        val dataTracks = playlistTrackDao.getTracksByEnterPlaylist(playlistId)
        Log.d("DEBUG_TRACKS", "DataTracks: $dataTracks")
        return dataTracks.map { dataTrack ->
            Track(
                trackId = dataTrack.trackId,
                trackName = dataTrack.trackName,
                artistName = dataTrack.artistName,
                trackTimeMillis = dataTrack.trackTimeMillis,
                artworkUrl100 = dataTrack.artworkUrl100,
                collectionName = dataTrack.collectionName,
                releaseDate = dataTrack.releaseDate?.let { Date(it) },
                primaryGenreName = dataTrack.primaryGenreName,
                country = dataTrack.country,
                previewUrl = dataTrack.previewUrl ?: "null",
                isFavorite = dataTrack.isFavorite
            )
        }
    }



}
