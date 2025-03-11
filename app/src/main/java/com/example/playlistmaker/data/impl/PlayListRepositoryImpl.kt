package com.example.playlistmaker.data.impl

import com.example.playlistmaker.data.db.playlist.PlaylistDao
import com.example.playlistmaker.data.db.track.TrackDao
import com.example.playlistmaker.domain.model.Playlist
import com.example.playlistmaker.domain.repository.PlayListRepository
import com.example.playlistmaker.mapper.PlayListMapper.toDomainModel
import com.example.playlistmaker.mapper.PlayListMapper.toEntity
import com.example.playlistmaker.mapper.PlayListMapper.toTrackCrossRefs


class PlayListRepositoryImpl(
    private val playListDao: PlaylistDao,
    private val trackDao: TrackDao
) : PlayListRepository {

    override suspend fun createPlaylist(playlist: Playlist) {
        val playlistEntity = playlist.toEntity()
        playListDao.insertPlaylist(playlistEntity)


        val crossRefs = playlist.toTrackCrossRefs()
        crossRefs.forEach { playListDao.addTrackToPlaylist(it) }
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        val playlistEntity = playlist.toEntity()
        playListDao.updatePlaylist(playlistEntity)


        playListDao.clearTracksFromPlaylist(playlist.id)
        val crossRefs = playlist.toTrackCrossRefs()
        crossRefs.forEach { playListDao.addTrackToPlaylist(it) }
    }

    override suspend fun getPlaylistById(id: Long): Playlist? {
        val playlistEntity = playListDao.getPlaylistById(id) ?: return null
        val trackIds = playListDao.getTrackIdsForPlaylist(id)
        val tracks = trackDao.getTracksByIds(trackIds).map { it.toDomain() }
        return playlistEntity.toDomainModel(tracks)
    }

    override suspend fun getAllPlaylists(): List<Playlist> {
        return playListDao.getAllPlaylists().map { entity ->
            val trackIds = playListDao.getTrackIdsForPlaylist(entity.playlistId)
            val tracks = trackDao.getTracksByIds(trackIds).map { it.toDomain() }
            entity.toDomainModel(tracks)
        }
    }
}
