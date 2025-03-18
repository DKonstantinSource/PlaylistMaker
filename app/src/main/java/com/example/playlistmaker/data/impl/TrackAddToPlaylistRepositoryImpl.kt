package com.example.playlistmaker.data.impl

import com.example.playlistmaker.data.db.AppDatabase
import com.example.playlistmaker.data.db.playlist.track_add_playlist.PlaylistTrackCrossRef
import com.example.playlistmaker.data.db.playlist.track_add_playlist.PlaylistTrackDao
import com.example.playlistmaker.data.db.playlist.track_add_playlist.PlaylistTrackEntity
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.domain.repository.TrackAddToPlaylistRepository

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

}
