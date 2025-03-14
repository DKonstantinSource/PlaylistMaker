package com.example.playlistmaker.presentation.fragments.player

import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.playlistmaker.Constants.FORMAT_TIME_TS
import com.example.playlistmaker.Constants.IS_FAVORITE
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlayerBinding
import com.example.playlistmaker.databinding.BottomSheetPlaylistsBinding
import com.example.playlistmaker.domain.api.MediaPlayerInteractor
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.presentation.view_model.player.PlayerViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import jp.wasabeef.glide.transformations.RoundedCornersTransformation
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.*

class FragmentPlayer : Fragment(R.layout.fragment_player) {
    private lateinit var binding: FragmentPlayerBinding
    private lateinit var bottomSheetBinding: BottomSheetPlaylistsBinding
    private val viewModel: PlayerViewModel by viewModel()
    private val mediaPlayerInteractorImpl: MediaPlayerInteractor by inject()
    private lateinit var bottomSheetDialog: BottomSheetDialog
    private lateinit var playlistAdapter: PlaylistAdapterPlayer

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentPlayerBinding.bind(view)

        setupBottomSheet()
        setupRecyclerView()

        val isFavorite = arguments?.getBoolean(IS_FAVORITE, false) ?: false
        updateFavoriteButton(isFavorite)


        val track = arguments?.getSerializable(TRACK_DATA) as? Track
        track?.let {
            viewModel.setTrack(it)
            updateUI(it)
        }

        viewModel.currentTrackTime.observe(viewLifecycleOwner) { time ->
            binding.currentTrackTime.text = time
        }

        viewModel.isPlayingLiveData.observe(viewLifecycleOwner) { isPlaying ->
            updatePlayButton(isPlaying)
        }

        viewModel.isFavorite.observe(viewLifecycleOwner) { isFavorite ->
            updateFavoriteButton(isFavorite)
        }

        viewModel.addTrackStatus.observe(viewLifecycleOwner) { message ->
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        }

        binding.backButton.setOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.playButton.setOnClickListener {
            viewModel.playbackControl()
        }

        binding.favoriteButton.setOnClickListener {
            viewModel.onFavoriteClicked()
        }

        viewModel.playlists.observe(viewLifecycleOwner, Observer { playlists ->
            playlistAdapter.submitList(playlists)
        })
        binding.buttonAddCollection.setOnClickListener {
            bottomSheetDialog.show()
        }

        bottomSheetBinding.buttonNewListBottomSheet.setOnClickListener {
            findNavController().navigate(R.id.fragmentPlayListAdd)
            bottomSheetDialog.dismiss()
        }

        bottomSheetBinding.buttonNewListBottomSheet.setOnClickListener {
            if (bottomSheetDialog.isShowing) {
                bottomSheetDialog.dismiss()
            } else {
                bottomSheetDialog.show()
            }
        }
    }

    private fun setupBottomSheet() {
        bottomSheetBinding = BottomSheetPlaylistsBinding.inflate(layoutInflater)
        bottomSheetDialog = BottomSheetDialog(requireContext())


        bottomSheetDialog.setContentView(bottomSheetBinding.root)

        val bottomSheetContainer = bottomSheetDialog.findViewById<View>(R.id.bottom_sheet_container)

        if (bottomSheetContainer != null) {
            val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetContainer)
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

            bottomSheetBinding.buttonNewListBottomSheet.setOnClickListener {
                findNavController().navigate(R.id.fragmentPlayListAdd)
                bottomSheetDialog.dismiss()
            }

            bottomSheetBehavior.addBottomSheetCallback(object :
                BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                        binding.overlay.visibility = View.GONE
                    } else {
                        binding.overlay.visibility = View.VISIBLE
                    }
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    binding.overlay.alpha = slideOffset
                }
            })

            binding.overlay.setOnClickListener {
                bottomSheetDialog.dismiss()
            }
        } else {
            Log.e("FragmentPlayer", "bottomSheetContainer is null")
        }
    }




    private fun setupRecyclerView() {
        playlistAdapter = PlaylistAdapterPlayer { playlist ->
            val track = viewModel.trackInfo.value
            if (track != null) {
                viewModel.addTrackToPlaylist(track, playlist)
                bottomSheetDialog.dismiss()
            }
        }
        bottomSheetBinding.rvPlaylists.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = playlistAdapter
        }
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

        Glide.with(requireContext())
            .load(artworkUrl)
            .placeholder(R.drawable.image_placeholder)
            .apply(RequestOptions.bitmapTransform(RoundedCornersTransformation(cornerRadius, 0)))
            .into(binding.cover)
    }

    private fun updatePlayButton(isPlaying: Boolean) {
        binding.playButton.setBackgroundResource(
            if (isPlaying) R.drawable.image_button_pause else R.drawable.image_play_button
        )
    }

    private fun updateFavoriteButton(isFavorite: Boolean) {
        binding.favoriteButton.setImageResource(
            if (isFavorite) R.drawable.favorit_is_clicked_icon else R.drawable.image_favorite_track_unclicked
        )
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

    private fun formatReleaseDate(releaseDate: Date?): String {
        return releaseDate?.let {
            val dateFormat = SimpleDateFormat(PATTERN_DATE_FORMAT, Locale.getDefault())
            dateFormat.format(it)
        } ?: getString(R.string.not_specified)
    }

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
