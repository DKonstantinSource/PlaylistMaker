package com.example.playlistmaker.presentation.ui.player

import NetworkUtils
import android.annotation.SuppressLint
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.util.TypedValue
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
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

    private val viewModel: PlayerViewModel by viewModel<PlayerViewModel>()
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
            updatePlayButton()
        }

        binding.backButton.setOnClickListener {
            mediaPlayerInteractorImpl.stop()
            viewModel.playbackControl()
            onBackPressed()
        }

        binding.playButton.setOnClickListener {
            viewModel.playbackControl()
            updatePlayButton()
        }
        updatePlayButton()
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

    private fun updatePlayButton() {
        if (viewModel.isPlaying()) {
            binding.playButton.setBackgroundResource(R.drawable.image_button_pause)
        } else {
            binding.playButton.setBackgroundResource(R.drawable.image_play_button)
        }
    }

    private fun turnOffScreen() {
        screenReceiver.playbackCallback = { isScreenOff ->
            if (isScreenOff) {
                mediaPlayerInteractorImpl.stop()
                viewModel.playbackControl()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        mediaPlayerInteractorImpl.stop()
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
        const val FORMAT_TIME_TS = "%02d:%02d"
        const val PATTERN_DATE_FORMAT = "yyyy"
        const val TRACK_DATA = "TRACK_DATA"
    }
}