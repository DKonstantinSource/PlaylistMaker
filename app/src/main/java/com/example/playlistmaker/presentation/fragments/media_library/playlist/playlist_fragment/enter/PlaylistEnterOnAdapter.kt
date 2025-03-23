package com.example.playlistmaker.presentation.fragments.media_library.playlist.playlist_fragment.enter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.model.Track

class PlaylistEnterOnAdapter(
    private val trackList: MutableList<Track>,
    private val onTrackClick: (Track) -> Unit,
    private val onTrackLongClick: (Track) -> Unit
) : RecyclerView.Adapter<PlaylistEnterOnAdapter.TrackViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.track_item, parent, false)
        return TrackViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        val track = trackList[position]
        holder.bind(track)
    }

    override fun getItemCount(): Int = trackList.size

    fun updateTracks(newTracks: List<Track>) {
        trackList.clear()
        trackList.addAll(newTracks)
        notifyDataSetChanged()
    }

    private fun formatTrackTime(milliseconds: Int): String {
        val totalSeconds = milliseconds / 1000
        val minutes = (totalSeconds / 60) % 60
        val seconds = totalSeconds % 60
        return String.format("%02d:%02d", minutes, seconds)
    }


    inner class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val trackName: TextView = itemView.findViewById(R.id.track_name)
        private val trackArtist: TextView = itemView.findViewById(R.id.name_artist)
        private val trackImage: ImageView = itemView.findViewById(R.id.track_image)
        private val trackTime: TextView = itemView.findViewById(R.id.track_time)

        fun bind(track: Track) {
            trackName.text = track.trackName
            trackArtist.text = track.artistName
            trackTime.text = formatTrackTime(track.trackTimeMillis)
            Glide.with(itemView.context)
                .load(track.artworkUrl100)
                .placeholder(R.drawable.image_placeholder)
                .into(trackImage)

            itemView.setOnClickListener { onTrackClick(track) }
            itemView.setOnLongClickListener {
                onTrackLongClick(track)
                true
            }
        }
    }
}

