package com.example.playlistmaker.data.impl

import com.example.playlistmaker.data.db.playlist.PlaylistDao
import com.example.playlistmaker.data.db.playlist.track_add_playlist.PlaylistTrackDao
import com.example.playlistmaker.data.db.playlist.track_add_playlist.PlaylistTrackEntity
import com.example.playlistmaker.data.db.track.TrackDao
import com.example.playlistmaker.domain.model.Playlist
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.domain.repository.PlayListRepository
import com.example.playlistmaker.mapper.PlayListMapper.toDomainModel
import com.example.playlistmaker.mapper.PlayListMapper.toEntity
import com.example.playlistmaker.mapper.PlayListMapper.toTrackCrossRefs
import com.example.playlistmaker.mapper.TrackMapperEntity

class PlayListRepositoryImpl(
    private val playListDao: PlaylistDao,
    private val trackDao: TrackDao,
    private val playlistTrackDao: PlaylistTrackDao
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

        // Создание сущности для трека
        val trackEntity = PlaylistTrackEntity(
            trackId = track.trackId,
            trackName = track.trackName,
            artistName = track.artistName,
            previewUrl = track.previewUrl
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
        crossRefs.forEach { crossRef ->
            playListDao.addTrackToPlaylist(crossRef)
        }
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        val playlistEntity = playlist.toEntity()
        playListDao.updatePlaylist(playlistEntity)
        playListDao.clearTracksFromPlaylist(playlist.id)
        val crossRefs = playlist.toTrackCrossRefs()
        crossRefs.forEach { crossRef ->
            playListDao.addTrackToPlaylist(crossRef)
        }
    }

    override suspend fun getPlaylistById(id: Long): Playlist? {
        val playlistEntity = playListDao.getPlaylistById(id) ?: return null
        val trackIds = playListDao.getTrackIdsForPlaylist(id)

        val tracks = if (trackIds.isNotEmpty()) {
            trackDao.getTracksByIds(trackIds).map { TrackMapperEntity.map(it) }
        } else {
            emptyList()
        }

        return playlistEntity.toDomainModel(tracks)
    }

    override suspend fun getAllPlaylists(): List<Playlist> {
        return playListDao.getAllPlaylists().map { entity ->
            val trackIds = playListDao.getTrackIdsForPlaylist(entity.playlistId)
            val tracks = if (trackIds.isNotEmpty()) {
                trackDao.getTracksByIds(trackIds).map { TrackMapperEntity.map(it) }
            } else {
                emptyList()
            }

            entity.toDomainModel(tracks)
        }
    }
}
