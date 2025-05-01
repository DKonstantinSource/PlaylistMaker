package com.example.playlistmaker.presentation.fragments.player

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
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
import com.example.playlistmaker.presentation.fragments.player.service.AudioPlayerService
import com.example.playlistmaker.presentation.fragments.player.service.AudioPlayerServiceInterface
import com.example.playlistmaker.presentation.ui.host.HostActivity
import com.example.playlistmaker.presentation.view_model.player.PlayerViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
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

    private var audioService: AudioPlayerServiceInterface? = null
    private var serviceConnection: ServiceConnection? = null
    private var isBound = false

    private val screenReceiver = ScreenReceiver()

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

        val track = arguments?.getParcelable(TRACK_DATA) as? Track
        track?.let {
            viewModel.setTrack(it)
            updateUI(it)
        }

        val intent = Intent(requireContext(), AudioPlayerService::class.java).apply {
            putExtra("track", track)
        }
        requireContext().bindService(intent, createServiceConnection(), Context.BIND_AUTO_CREATE)



        setupBottomSheet()
        setupRecyclerView()
        observeState()
        bindServiceToAudioPlayer()
        viewModel.currentTrackTime.observe(viewLifecycleOwner) { time ->
            binding.currentTrackTime.text = time
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
            observeService()
            audioService?.togglePlayback()

        }

        viewModel.isPlayingLiveData.observe(viewLifecycleOwner) { isPlaying ->
            binding.playButton.setPlaying(isPlaying)

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

    private fun createServiceConnection(): ServiceConnection {
        return object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                if (service is AudioPlayerService.AudioPlayerBinder) {
                    audioService = service.getService()
                    isBound = true

                    val track = viewModel.trackInfo.value
                    if (track != null) {
                        audioService?.preparePlayer(track)
                    }

                    observeService()
                }
            }

            override fun onServiceDisconnected(name: ComponentName?) {
                audioService = null
                isBound = false
            }
        }
    }

    private fun bindServiceToAudioPlayer() {
        val track = viewModel.trackInfo.value
        val intent = Intent(requireContext(), AudioPlayerService::class.java).apply {
            putExtra("track", track)
        }

        requireContext().bindService(intent, createServiceConnection(), Context.BIND_AUTO_CREATE)
    }



    private var isObservingService = false

    private fun observeService() {
        if (isObservingService) return
        isObservingService = true

        lifecycleScope.launch {
            audioService?.getIsPlaying()?.collect { isPlaying ->
                binding.playButton.setPlaying(isPlaying)
            }
        }

        lifecycleScope.launch {
            audioService?.getCurrentTime()?.collect { time ->
                binding.currentTrackTime.text = time
            }
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

    private fun updateFavoriteButton(isFavorite: Boolean) {
        binding.favoriteButton.setImageResource(
            if (isFavorite) R.drawable.favorit_is_clicked_icon else R.drawable.image_favorite_track_unclicked
        )
    }
    override fun onResume() {
        super.onResume()
        audioService?.hideNotification()
        viewModel.refreshPlaylists()
        binding.playButton.setPlaying(audioService?.getIsPlaying()?.value == true)
    }

    override fun onPause() {
        super.onPause()
        if (audioService?.getIsPlaying()?.value == true) {
            audioService?.showNotificationIfPlaying()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        audioService?.stop()
        if (isBound && serviceConnection != null) {
            requireContext().unbindService(serviceConnection!!)
            isBound = false
        }
        _binding = null
        _bottomSheetBinding = null


    }

    override fun onDestroy() {
        super.onDestroy()

        audioService?.stop()
        _binding = null
        _bottomSheetBinding = null
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