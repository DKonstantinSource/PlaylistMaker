package com.example.playlistmaker.mapper

import com.example.playlistmaker.data.db.playlist.PlaylistEntity
import com.example.playlistmaker.data.db.playlist.track_add_playlist.PlaylistTrackCrossRef
import com.example.playlistmaker.domain.model.Playlist
import com.example.playlistmaker.domain.model.Track
import com.google.gson.Gson

object PlayListMapper {

    fun PlaylistEntity.toDomainModel(tracks: List<Track>): Playlist {
        return Playlist(
            id = this.playlistId,
            name = this.name,
            description = this.description,
            imagePath = this.imagePath,
            tracks = tracks,
            trackCount = tracks.size
        )
    }


    fun Playlist.toEntity(): PlaylistEntity {
        return PlaylistEntity(
            playlistId = this.id,
            name = this.name,
            description = this.description,
            imagePath = this.imagePath,
            trackCount = this.tracks.size,
            tracks = Gson().toJson(this.tracks)
        )
    }

    fun Playlist.toTrackCrossRefs(): List<PlaylistTrackCrossRef> {
        return this.tracks.map { track ->
            PlaylistTrackCrossRef(
                playlistId = this.id,
                trackId = track.trackId
            )
        }
    }

}