package com.example.playlistmaker

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PlayerActivity : AppCompatActivity() {
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

    @SuppressLint("MissingInflatedId")
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
        addToPlaylistButton = findViewById(R.id.buttonAddCollection)
        addToFavoritesButton = findViewById(R.id.favorite_button)


        val track = intent.getSerializableExtra(TRACK_DATA) as? Track

        track?.let {
            updateUI(it)
        }

        backButton.setOnClickListener {
            onBackPressed()
        }
    }

    private fun updateUI(track: Track) {
        trackNameTextView.text = track.trackName
        artistNameTextView.text = track.artistName
        collectionNameTextView.text = track.collectionName

        releaseDateTextView.text = formatReleaseDate(track.releaseDate)
        primaryGenreNameTextView.text = track.primaryGenreName
        countryTextView.text = track.country
        trackTimeTextView.text = formatTrackTime(track.trackTimeMillis.toLong())

        val artworkUrl = track.getCoverArtwork()
        Glide.with(this)
            .load(artworkUrl)
            .placeholder(R.drawable.image_placeholder)
            .into(coverImageView)
    }


    private fun formatReleaseDate(releaseDate: Date?): String {
        if (releaseDate == null) {
            return "Не указано"
        }

        val dateFormat = SimpleDateFormat(PATTERN_DATE_FORMAT, Locale.getDefault())
        return dateFormat.format(releaseDate)
    }

    private fun formatTrackTime(millis: Long): String {
        val seconds = (millis / 1000) % 60
        val minutes = (millis / (1000 * 60)) % 60
        return String.format(FORMAT_TIME_TS, minutes, seconds)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    companion object {
        const val FORMAT_TIME_TS = "%02d:%02d"
        const val PATTERN_DATE_FORMAT = "yyyy"
        const val TRACK_DATA = "TRACK_DATA"
    }
}