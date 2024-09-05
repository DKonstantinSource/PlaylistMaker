package com.example.playlistmaker
import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners


class TracksViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val trackName: TextView = itemView.findViewById(R.id.track_name)
    private val artistName: TextView = itemView.findViewById(R.id.name_artist)
    private val trackTime: TextView = itemView.findViewById(R.id.track_time)
    private val trackLogo: ImageView = itemView.findViewById(R.id.track_image)

    fun bind(model: Track) {
        trackName.text = model.trackName
        artistName.text = model.artistName
        trackTime.text = model.trackTime

        if (NetworkUtil.isNetworkAvailable(itemView.context)) {
            Glide.with(itemView)
                .load(model.art_image)
                .fitCenter()
                .placeholder(R.drawable.light_mode_search)
                .centerCrop()
                .transform(RoundedCorners(Utils.dpToPx(2f, itemView.context)))
                .into(trackLogo)
        } else {
            trackLogo.setImageResource(R.drawable.ic_launcher_background)
        }


//        Glide.with(itemView)
//            .load(model.art_image)
//            .fitCenter()
//            .placeholder(R.drawable.light_mode_search)
//            .centerCrop()
//            .transform(RoundedCorners(Utils.dpToPx(2f, itemView.context)))
//            .into(trackLogo)
//    }
    }
}