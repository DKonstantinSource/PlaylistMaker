package com.example.playlistmaker.presentation.ui.player

import android.annotation.SuppressLint
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.util.TypedValue
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.playlistmaker.Constants.FORMAT_TIME_TS
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityPlayerBinding
import com.example.playlistmaker.domain.api.MediaPlayerInteractor
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.presentation.view_model.player.PlayerViewModel
import jp.wasabeef.glide.transformations.RoundedCornersTransformation
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.Date
import java.util.Locale

class PlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlayerBinding

    private val viewModel: PlayerViewModel by viewModel()
    private val mediaPlayerInteractorImpl: MediaPlayerInteractor by inject()
    private val screenReceiver: ScreenReceiver by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val track = intent.getSerializableExtra(TRACK_DATA) as? Track
        track?.let {
            viewModel.setTrack(it)
            updateUI(it)
        }

        viewModel.currentTrackTime.observe(this) { time ->
            binding.currentTrackTime.text = time
        }

        viewModel.isPlayingLiveData.observe(this) { isPlaying ->
            updatePlayButton(isPlaying)
        }

        viewModel.isFavorite.observe(this) { isFavorite ->
            updateFavoriteButton(isFavorite)
        }

        binding.backButton.setOnClickListener {
            onBackPressed()
        }

        binding.playButton.setOnClickListener {
            viewModel.playbackControl()
        }

        binding.favoriteButton.setOnClickListener {
            viewModel.onFavoriteClicked()
        }

        turnOffScreen()
    }

    private fun updateUI(track: Track) {
        binding.trackNamePlayer.text = track.trackName
        binding.artistName.text = track.artistName
        binding.collectionNameValue.text = track.collectionName
        binding.releaseDateValue.text = formatReleaseDate(track.releaseDate)
        binding.trackGenreValue.text = track.primaryGenreName
        binding.countryValue.text = track.country
        binding.durationValue.text = formatTrackTime(track.trackTimeMillis.toLong())

        val artworkUrl = track.getCoverArtwork()
        val cornerRadius = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            8f,
            binding.cover.resources.displayMetrics
        ).toInt()

        Glide.with(this)
            .load(artworkUrl)
            .placeholder(R.drawable.image_placeholder)
            .apply(RequestOptions.bitmapTransform(RoundedCornersTransformation(cornerRadius, 0)))
            .into(binding.cover)
    }

    private fun updatePlayButton(isPlaying: Boolean) {
        if (isPlaying) {
            binding.playButton.setBackgroundResource(R.drawable.image_button_pause)
        } else {
            binding.playButton.setBackgroundResource(R.drawable.image_play_button)
        }
    }

    private fun updateFavoriteButton(isFavorite: Boolean) {
        val iconRes =
            if (isFavorite) R.drawable.favorit_is_clicked_icon else R.drawable.image_favorite_track_unclicked
        binding.favoriteButton.setImageResource(iconRes)
    }

    private fun turnOffScreen() {
        screenReceiver.playbackCallback = { isScreenOff ->
            if (isScreenOff) {
                mediaPlayerInteractorImpl.stop()
                viewModel.pausePlayer()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.cleanup()
        mediaPlayerInteractorImpl.stop()
    }

    @Deprecated("This method use back button.")
    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    private fun formatReleaseDate(releaseDate: Date?): String {
        return releaseDate?.let {
            val dateFormat = SimpleDateFormat(PATTERN_DATE_FORMAT, Locale.getDefault())
            dateFormat.format(it)
        } ?: getString(R.string.not_specified)
    }

    @SuppressLint("DefaultLocale")
    private fun formatTrackTime(millis: Long): String {
        val seconds = (millis / 1000) % 60
        val minutes = (millis / (1000 * 60)) % 60
        return String.format(FORMAT_TIME_TS, minutes, seconds)
    }

    companion object {
        const val PATTERN_DATE_FORMAT = "yyyy"
        const val TRACK_DATA = "TRACK_DATA"
    }
}
