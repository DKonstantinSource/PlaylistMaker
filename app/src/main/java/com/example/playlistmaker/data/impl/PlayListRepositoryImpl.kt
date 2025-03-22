package com.example.playlistmaker.data.impl

import android.util.Log
import com.example.playlistmaker.data.db.playlist.PlaylistDao
import com.example.playlistmaker.data.db.playlist.track_add_playlist.PlaylistTrackCrossRefDao
import com.example.playlistmaker.data.db.playlist.track_add_playlist.PlaylistTrackDao
import com.example.playlistmaker.data.db.playlist.track_add_playlist.PlaylistTrackEntity
import com.example.playlistmaker.data.db.track.TrackDao
import com.example.playlistmaker.domain.model.Playlist
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.domain.repository.PlayListRepository
import com.example.playlistmaker.mapper.PlayListMapper.toDomainModel
import com.example.playlistmaker.mapper.PlayListMapper.toEntity
import com.example.playlistmaker.mapper.PlayListMapper.toTrackCrossRefs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.coroutines.cancellation.CancellationException

class PlayListRepositoryImpl(
    private val playListDao: PlaylistDao,
    private val trackDao: TrackDao,
    private val playlistTrackDao: PlaylistTrackDao,
    private val playlistTrackCrossRefDao: PlaylistTrackCrossRefDao
) : PlayListRepository {

    override suspend fun addTrackToPlaylist(track: Track, playlistId: Long) {
        val playlistEntity = playListDao.getPlaylistById(playlistId) ?: return
        val updatedTrackIds = playlistEntity.tracks.split(",")
            .mapNotNull { it.toLongOrNull() }
            .toMutableList()

        if (!updatedTrackIds.contains(track.trackId)) {
            updatedTrackIds.add(track.trackId)
        }
        playListDao.updatePlaylistTracks(playlistId, updatedTrackIds.joinToString(","))
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
            addedDate = System.currentTimeMillis()
        )

        playlistTrackDao.insertTrack(trackEntity)
        playListDao.updateTrackCount(playlistId, updatedTrackIds.size)
    }

    override suspend fun removeTrackFromPlaylist(playlistId: Long, trackId: Long) {
        playListDao.removeTrackFromPlaylist(playlistId, trackId)
    }

    override suspend fun createPlaylist(playlist: Playlist) {
        val playlistEntity = playlist.toEntity()
        playListDao.insertPlaylist(playlistEntity)

        val crossRefs = playlist.toTrackCrossRefs()
        crossRefs.forEach { playlistTrackCrossRefDao.insertCrossRef(it) }
        playListDao.updateTrackCount(playlist.id, playlist.tracks.size)
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        val playlistEntity = playlist.toEntity()
        playListDao.updatePlaylist(playlistEntity)
    }

    override suspend fun getPlaylistById(id: Long): Playlist? {
        val playlistEntity = playListDao.getPlaylistById(id) ?: run {
            Log.e("DEBUG_TRACKS", "Playlist not found for id: $id")
            return null
        }
        Log.d("DEBUG_TRACKS", "Playlist foud: $playlistEntity")

        val trackIds = playListDao.getTrackIdsForPlaylist(id)
        Log.d("DEBUG_TRACKS", "Track IDs for playlist: $trackIds")

        val tracks = if (trackIds.isNotEmpty()) {
            trackDao.getTracksByIds(trackIds).map { it.toDomain() }
        } else {
            emptyList()
        }

        Log.d("DEBUG_TRACKS", "Tracks: $tracks")

        return playlistEntity.toDomainModel(tracks, tracks.size)
    }


    override suspend fun getAllPlaylists(): List<Playlist> {
        val playlists = playListDao.getAllPlaylists()

        return playlists.map { playlistEntity ->
            val trackIds =
                playlistTrackCrossRefDao.getTrackIdsForPlaylist(playlistEntity.playlistId)

            val tracks = if (trackIds.isNotEmpty()) {
                trackDao.getTracksByIds(trackIds).map { it.toDomain() }
            } else {
                emptyList()
            }
            val trackCount = trackIds.size

            playlistEntity.toDomainModel(tracks, trackCount)
        }
    }

    override suspend fun deletePlaylist(playlistId: Long) {
        withContext(Dispatchers.IO) {
            playlistTrackCrossRefDao.deleteByPlaylistId(playlistId)
            playListDao.deletePlaylistById(playlistId)

        }
    }
}
