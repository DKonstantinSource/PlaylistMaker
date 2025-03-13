package com.example.playlistmaker.presentation.ui.player

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.model.Playlist
import java.io.File


class PlaylistAdapterPlayer(
    private val onPlaylistClick: (Playlist) -> Unit
) : ListAdapter<Playlist, PlaylistAdapterPlayer.PlaylistViewHolder>(PlaylistDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.bottom_sheet_item, parent, false)
        return PlaylistViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class PlaylistViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val playlistImage: ImageView = itemView.findViewById(R.id.playlistImageSheet)
        private val playlistName: TextView = itemView.findViewById(R.id.playlistNameSheet)
        private val playlistTrackCount: TextView =
            itemView.findViewById(R.id.playlistTrackCountSheet)

        fun bind(playlist: Playlist) {
            playlistName.text = playlist.name
            playlistTrackCount.text = "${playlist.trackCount} треков"
            if (!playlist.imagePath.isNullOrEmpty()) {
                Glide.with(itemView)
                    .load(File(playlist.imagePath))
                    .placeholder(R.drawable.image_placeholder)
                    .into(playlistImage)
            } else {
                playlistImage.setImageResource(R.drawable.image_placeholder)
            }

            itemView.setOnClickListener { onPlaylistClick(playlist) }
        }
    }
}

class PlaylistDiffCallback : DiffUtil.ItemCallback<Playlist>() {
    override fun areItemsTheSame(oldItem: Playlist, newItem: Playlist): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Playlist, newItem: Playlist): Boolean {
        return oldItem == newItem
    }
}
