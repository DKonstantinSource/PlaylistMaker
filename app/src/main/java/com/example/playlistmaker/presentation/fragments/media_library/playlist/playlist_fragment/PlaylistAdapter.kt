package com.example.playlistmaker.presentation.fragments.media_library.playlist.playlist_fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.playlistmaker.GlideUtils
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.PlaylistItemBinding
import com.example.playlistmaker.domain.model.Playlist

class PlaylistAdapter(private val onItemClick: (Long) -> Unit) :
    ListAdapter<Playlist, PlaylistAdapter.PlaylistViewHolder>(PlaylistDiffCallback()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val binding =
            PlaylistItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlaylistViewHolder(binding)

    }

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        val playlist = getItem(position)
        holder.bind(playlist)
    }

    inner class PlaylistViewHolder(private val binding: PlaylistItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val playlist = getItem(adapterPosition)
                onItemClick(playlist.id)
            }
        }

        fun bind(playlist: Playlist) {
            binding.playlistName.text = playlist.name
            binding.playListCount.text = "${playlist.trackCount} треков"

            val imagePath = playlist.imagePath
            val cornerRadius = GlideUtils.dpToPx(8f, itemView.context)

            Glide.with(itemView)
                .load(if (imagePath.isNullOrEmpty()) R.drawable.empty_placeholder_big else imagePath)
                .apply(
                    RequestOptions()
                        .transform(RoundedCorners(cornerRadius))
                        .also {
                            if (imagePath.isNullOrEmpty() || imagePath.isBlank()) {
                                it.centerInside()
                            } else {
                                it.centerCrop()
                            }
                        }
                )
                .placeholder(R.drawable.empty_placeholder_big)
                .error(R.drawable.empty_placeholder_big)
                .into(binding.playlistCoverImage)
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
}
