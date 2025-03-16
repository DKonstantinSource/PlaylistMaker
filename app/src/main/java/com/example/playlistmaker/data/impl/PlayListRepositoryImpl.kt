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

class PlayListRepositoryImpl(
    private val playListDao: PlaylistDao,
    private val trackDao: TrackDao,
    private val playlistTrackDao: PlaylistTrackDao
) : PlayListRepository {

    override suspend fun addTrackToPlaylist(track: Track, playlistId: Long) {
        val playlistEntity = playListDao.getPlaylistById(playlistId) ?: return

        // Преобразуем строку с trackId в список Long
        val updatedTrackIds = playlistEntity.tracks.split(",")
            .mapNotNull { it.toLongOrNull() }
            .toMutableList()

        // Добавляем новый трек
        if (!updatedTrackIds.contains(track.trackId)) {
            updatedTrackIds.add(track.trackId)
        }

        // Обновляем плейлист в базе данных
        playListDao.updatePlaylistTracks(playlistId, updatedTrackIds.joinToString(","))

        // Добавляем трек в базу данных, если его там нет
        val trackEntity = PlaylistTrackEntity(
            trackId = track.trackId,
            trackName = track.trackName,
            artistName = track.artistName,
            previewUrl = track.previewUrl
        )
        playlistTrackDao.insertTrack(trackEntity)

        // Обновляем количество треков
        playListDao.updateTrackCount(playlistId, updatedTrackIds.size)
    }

    override suspend fun removeTrackFromPlaylist(playlistId: Long, trackId: Long) {
        // Удаляем связь трека с плейлистом
        playListDao.removeTrackFromPlaylist(playlistId, trackId)
    }

    override suspend fun createPlaylist(playlist: Playlist) {
        val playlistEntity = playlist.toEntity()
        playListDao.insertPlaylist(playlistEntity)

        val crossRefs = playlist.toTrackCrossRefs()
        crossRefs.forEach { playListDao.addTrackToPlaylist(it) }
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        val playlistEntity = playlist.toEntity()
        playListDao.updatePlaylist(playlistEntity)

        // Очищаем старые треки
        playListDao.clearTracksFromPlaylist(playlist.id)

        // Добавляем новые связи между плейлистом и треками
        val crossRefs = playlist.toTrackCrossRefs()
        crossRefs.forEach { playListDao.addTrackToPlaylist(it) }
    }

    override suspend fun getPlaylistById(id: Long): Playlist? {
        val playlistEntity = playListDao.getPlaylistById(id) ?: return null
        val trackIds = playListDao.getTrackIdsForPlaylist(id)

        // Если список trackIds пуст, возвращаем пустой список
        val tracks = if (trackIds.isNotEmpty()) {
            trackDao.getTracksByIds(trackIds).map { it.toDomain() }
        } else {
            emptyList()
        }

        return playlistEntity.toDomainModel(tracks)
    }

    override suspend fun getAllPlaylists(): List<Playlist> {
        return playListDao.getAllPlaylists().map { entity ->
            val trackIds = playListDao.getTrackIdsForPlaylist(entity.playlistId)

            // Если trackIds пуст, просто возвращаем пустой список треков
            val tracks = if (trackIds.isNotEmpty()) {
                trackDao.getTracksByIds(trackIds).map { it.toDomain() }
            } else {
                emptyList()
            }

            entity.toDomainModel(tracks)
        }
    }
}
