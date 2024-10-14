package com.example.playlistmaker

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

class TrackAdapter(
    private var tracks: List<Track> = listOf(),
    private val onTrackClick: (Track) -> Unit
) : RecyclerView.Adapter<TrackAdapter.TracksViewHolder>() {

    inner class TracksViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val trackName: TextView = itemView.findViewById(R.id.track_name)
        private val artistName: TextView = itemView.findViewById(R.id.name_artist)
        private val trackTime: TextView = itemView.findViewById(R.id.track_time)
        private val trackLogo: ImageView = itemView.findViewById(R.id.track_image)

        private fun formatTrackTime(milliseconds: String): String {
            val totalSeconds = milliseconds.toLong().div(1000)
            val minutes = (totalSeconds / 60) % 60
            val seconds = totalSeconds % 60
            return String.format("%02d:%02d", minutes, seconds)
        }

        fun bind(model: Track) {
            trackName.text = model.trackName
            artistName.text = model.artistName
            trackTime.text = formatTrackTime(model.trackTimeMillis)
            artistName.requestLayout()
            Glide.with(itemView.context)
                .load(model.artworkUrl100)
                .fitCenter()
                .placeholder(R.drawable.image_placeholder)
                .centerCrop()
                .transform(RoundedCorners(Utils.dpToPx(2f, itemView.context)))
                .into(trackLogo)


            itemView.setOnClickListener {
                onTrackClick(model)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TracksViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.track_item, parent, false)
        return TracksViewHolder(view)
    }

    override fun getItemCount(): Int {
        return tracks.size
    }

    override fun onBindViewHolder(holder: TracksViewHolder, position: Int) {
        holder.bind(tracks[position])
        holder.itemView.setOnClickListener {
            onTrackClick(tracks[position])
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newTracks: List<Track>) {
        tracks = newTracks
        notifyDataSetChanged()
    }
//

}

