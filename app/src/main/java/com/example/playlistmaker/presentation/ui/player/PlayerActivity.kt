package com.example.playlistmaker.presentation.ui.player


import android.annotation.SuppressLint
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.util.TypedValue
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.playlistmaker.Creator.Creator
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.api.MediaPlayerInteractor
import com.example.playlistmaker.domain.model.PlayerState
import com.example.playlistmaker.domain.model.Track
import jp.wasabeef.glide.transformations.RoundedCornersTransformation
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class PlayerActivity : AppCompatActivity() {

    private lateinit var mediaPlayerInteractorImpl: MediaPlayerInteractor
    private lateinit var screenReceiver: ScreenReceiver
    private lateinit var handler: Handler
    private var songBridge: String? = null
    private var playerState = PlayerState.DEFAULT

    private lateinit var backButton: ImageButton
    private lateinit var coverImageView: ImageView
    private lateinit var trackNameTextView: TextView
    private lateinit var artistNameTextView: TextView
    private lateinit var collectionNameTextView: TextView
    private lateinit var releaseDateTextView: TextView
    private lateinit var primaryGenreNameTextView: TextView
    private lateinit var countryTextView: TextView
    private lateinit var trackTimeTextView: TextView
    private lateinit var playButton: ImageButton
    private lateinit var addToPlaylistButton: ImageButton
    private lateinit var addToFavoritesButton: ImageButton
    private lateinit var currentTrackTime: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)


        backButton = findViewById(R.id.backButton)
        coverImageView = findViewById(R.id.cover)
        trackNameTextView = findViewById(R.id.track_name_player)
        artistNameTextView = findViewById(R.id.artistName)
        collectionNameTextView = findViewById(R.id.collectionNameValue)
        releaseDateTextView = findViewById(R.id.releaseDateValue)
        primaryGenreNameTextView = findViewById(R.id.trackGenreValue)
        countryTextView = findViewById(R.id.countryValue)
        trackTimeTextView = findViewById(R.id.durationValue)
        playButton = findViewById(R.id.playButton)
        currentTrackTime = findViewById(R.id.currentTrackTime)
        addToPlaylistButton = findViewById(R.id.buttonAddCollection)
        addToFavoritesButton = findViewById(R.id.favorite_button)

        mediaPlayerInteractorImpl = Creator.createPlayer()
        handler = Handler(Looper.getMainLooper())

        screenReceiver = ScreenReceiver()
        val filter = IntentFilter(Intent.ACTION_SCREEN_OFF)
        registerReceiver(screenReceiver, filter)

        val track = intent.getSerializableExtra(TRACK_DATA) as? Track
        track?.let {
            updateUI(it)
            preparePlayer(it)
        }

        backButton.setOnClickListener {
            mediaPlayerInteractorImpl.stop()
            onBackPressed()
        }

        playButton.setOnClickListener {
            PlayerState.PREPARED
            PlayerState.PAUSED
            playbackControl()
            startCountdown()
        }
    }

    private fun startCountdown() {
        handler.post(object : Runnable {
            @SuppressLint("DefaultLocale")
            override fun run() {
                if (mediaPlayerInteractorImpl.isPlaying()) {
                    val currentPositionMillis = mediaPlayerInteractorImpl.getCurrentPosition()
                    val minutes = (currentPositionMillis / 1000) / 60
                    val seconds = (currentPositionMillis / 1000) % 60
                    val formattedTime = String.format("%02d:%02d", minutes, seconds)
                    currentTrackTime.text = formattedTime
                    handler.postDelayed(this, 1000)
                } else {
                    handler.removeCallbacks(this)
                    playButton.setBackgroundResource(R.drawable.image_play_button)
                    currentTrackTime.text = getString(R.string.placeholderCurrentTrack)
                }
            }
        })
    }

    private fun updateUI(track: Track) {
        trackNameTextView.text = track.trackName
        artistNameTextView.text = track.artistName
        collectionNameTextView.text = track.collectionName
        releaseDateTextView.text = formatReleaseDate(track.releaseDate)
        primaryGenreNameTextView.text = track.primaryGenreName
        countryTextView.text = track.country
        trackTimeTextView.text = formatTrackTime(track.trackTimeMillis.toLong())

        songBridge = track.previewUrl

        val artworkUrl = track.getCoverArtwork()

        val cornerRadius = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            8f,
            coverImageView.resources.displayMetrics
        ).toInt()

        Glide.with(this)
            .load(artworkUrl)
            .placeholder(R.drawable.image_placeholder)
            .apply(RequestOptions.bitmapTransform(RoundedCornersTransformation(cornerRadius, 0)))
            .into(coverImageView)
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

    @Deprecated("This method use back button.")
    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    private fun preparePlayer(track: Track) {
        songBridge = track.previewUrl

        if (!songBridge.isNullOrEmpty()) {
            mediaPlayerInteractorImpl.execute(track)
            playerState = PlayerState.PREPARED
        } else {
            Log.e("CheckBridgeUrl", "Url is empty")
        }
    }

    private fun startPlayer() {
        mediaPlayerInteractorImpl.play()
        playButton.setBackgroundResource(R.drawable.image_button_pause)
        playerState = PlayerState.PLAYING
    }

    private fun pausePlayer() {
        if (mediaPlayerInteractorImpl.isPlaying()) {
            mediaPlayerInteractorImpl.pause()
            playButton.setBackgroundResource(R.drawable.image_play_button)
            playerState = PlayerState.PAUSED
        }
    }

    private fun playbackControl() {
        when (playerState) {
            PlayerState.PLAYING -> pausePlayer()
            PlayerState.PREPARED, PlayerState.PAUSED -> startPlayer()
            PlayerState.DEFAULT -> {
                Log.e("ErrorState", "PlayerErrorState")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
        mediaPlayerInteractorImpl.stop()
        try {
            unregisterReceiver(screenReceiver)
        } catch (e: IllegalArgumentException) {
            Log.w("PlayerActivityError", "Receiver was not registered. Check Who False", e)
        }
    }

    override fun onStart() {
        super.onStart()
        registerReceiver(screenReceiver, IntentFilter(Intent.ACTION_SCREEN_OFF))
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(screenReceiver)
    }


    companion object {
        const val FORMAT_TIME_TS = "%02d:%02d"
        const val PATTERN_DATE_FORMAT = "yyyy"
        const val TRACK_DATA = "TRACK_DATA"
    }
}