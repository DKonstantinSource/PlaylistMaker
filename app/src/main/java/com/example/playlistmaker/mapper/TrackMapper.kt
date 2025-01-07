package com.example.playlistmaker.mapper


import android.util.Log
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.example.playlistmaker.data.model.Track as DataTrack
import com.example.playlistmaker.domain.model.Track as DomainTrack

object TrackMapper {
    fun map(dataTrack: DataTrack): DomainTrack {
        try {
            return DomainTrack(
                trackId = dataTrack.trackId,
                trackName = dataTrack.trackName,
                artistName = dataTrack.artistName,
                trackTimeMillis = dataTrack.trackTimeMillis,
                artworkUrl100 = dataTrack.artworkUrl100,
                collectionName = dataTrack.collectionName,
                releaseDate = dataTrack.releaseDate?.let {

                    val dateFormat =
                        SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.getDefault())
                    dateFormat.parse(it.toString())
                } ?: Date(),
                primaryGenreName = dataTrack.primaryGenreName,
                country = dataTrack.country,
                previewUrl = dataTrack.previewUrl ?: "null",
            )
        } catch (e: Exception) {
            Log.e("Exeption Check", e.toString())

        }
        return TODO("В данный момент я не сильно могу понять, как мне обойти кро ме как выбросить исключение, завтра в пачке спрошу, что и как ")
    }
}