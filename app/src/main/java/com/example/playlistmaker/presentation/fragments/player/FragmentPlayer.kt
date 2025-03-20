package com.example.playlistmaker.presentation.fragments.player

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.playlistmaker.GlideUtils
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.BottomSheetPlaylistsBinding
import com.example.playlistmaker.databinding.FragmentPlayerBinding
import com.example.playlistmaker.domain.api.MediaPlayerInteractor
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.presentation.ui.host.HostActivity
import com.example.playlistmaker.presentation.view_model.player.PlayerViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.*

class FragmentPlayer : Fragment(R.layout.fragment_player) {

    private var _binding: FragmentPlayerBinding? = null
    private val binding get() = _binding!!

    private var _bottomSheetBinding: BottomSheetPlaylistsBinding? = null
    private val bottomSheetBinding get() = _bottomSheetBinding!!

    private val viewModel: PlayerViewModel by viewModel()
    private val mediaPlayerInteractorImpl: MediaPlayerInteractor by inject()

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>
    private lateinit var playlistAdapter: PlaylistAdapterPlayer

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlayerBinding.inflate(inflater, container, false)
        _bottomSheetBinding =
            BottomSheetPlaylistsBinding.bind(binding.root.findViewById(R.id.bottom_sheet_new_playlist))

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as? HostActivity)?.setBottomNavigationVisibility(false)

        val isFavorite = arguments?.getBoolean(IS_FAVORITE, false) ?: false
        updateFavoriteButton(isFavorite)

        val track = arguments?.getSerializable(TRACK_DATA) as? Track
        track?.let {
            viewModel.setTrack(it)
            updateUI(it)
        }

        setupBottomSheet()
        setupRecyclerView()
        observeState()

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
            showSnackBar(message)
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

        binding.buttonAddCollection.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            viewModel.refreshPlaylists()
        }

        bottomSheetBinding.buttonNewListBottomSheet.setOnClickListener {
            findNavController().navigate(R.id.action_fragment_Player_to_fragmentPlayListAdd)
        }


    }

    private fun setupRecyclerView() {
        playlistAdapter = PlaylistAdapterPlayer { playlist ->
            val track = viewModel.trackInfo.value ?: return@PlaylistAdapterPlayer
            viewModel.addTrackToPlaylist(track, playlist.id)
        }


        bottomSheetBinding.rvPlaylists.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = playlistAdapter
        }
    }

    private fun setupBottomSheet() {
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetBinding.root as View).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        binding.overlay.setOnClickListener {
            if (bottomSheetBehavior.state != BottomSheetBehavior.STATE_HIDDEN) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            }
        }

        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                binding.overlay.visibility =
                    if (newState == BottomSheetBehavior.STATE_HIDDEN) View.GONE else View.VISIBLE
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                binding.overlay.alpha = ((slideOffset + 1) / 2).coerceIn(0f, 1f)
            }
        })
    }


    private fun observeState() {
        viewModel.playlists.observe(viewLifecycleOwner) { playlists ->
            playlistAdapter.submitList(playlists)
            bottomSheetBinding.rvPlaylists.isVisible = playlists.isNotEmpty()
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
        val cornerRadius = GlideUtils.dpToPx(8f, requireContext())

        Glide.with(requireContext())
            .load(artworkUrl)
            .placeholder(R.drawable.image_placeholder)
            .apply(RequestOptions().transform(RoundedCorners(cornerRadius)))
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
    override fun onResume() {
        super.onResume()
        viewModel.refreshPlaylists()
    }

    override fun onPause() {
        super.onPause()
        viewModel.pausePlayer()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        _bottomSheetBinding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.cleanup()
        (activity as? HostActivity)?.setBottomNavigationVisibility(true)
        mediaPlayerInteractorImpl.stop()
    }

    private fun formatReleaseDate(releaseDate: Date?): String {
        return releaseDate?.let {
            SimpleDateFormat(PATTERN_DATE_FORMAT, Locale.getDefault()).format(it)
        } ?: getString(R.string.not_specified)
    }

    private fun formatTrackTime(millis: Long): String {
        val minutes = (millis / 1000 / 60) % 60
        val seconds = (millis / 1000) % 60
        return String.format(FORMAT_TIME_TS, minutes, seconds)
    }

    private fun showSnackBar(message: String?) {
        message?.let {
            val snackbar = Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG)
            val textView =
                snackbar.view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
            val typeface = ResourcesCompat.getFont(requireContext(), R.font.ys_display_regular)
            textView.setTextSize(14f)
            textView.setTypeface(typeface)
            snackbar.show()
        }
    }


    companion object {
        const val PATTERN_DATE_FORMAT = "yyyy"
        const val TRACK_DATA = "TRACK_DATA"
        const val IS_FAVORITE = "IS_FAVORITE"
        const val FORMAT_TIME_TS = "%02d:%02d"
    }
}
