package com.example.playlistmaker.presentation.fragments.media_library.playlist.playlist_fragment.enter

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentEnterPlaylistBinding
import com.example.playlistmaker.databinding.BottomSheetEnterPlaylistBinding
import com.example.playlistmaker.databinding.BottomSheetEnterPlaylistEditBinding
import com.example.playlistmaker.domain.model.Playlist
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.domain.settings.sharing.api.ExternalNavigatorInteractor
import com.example.playlistmaker.presentation.fragments.player.FragmentPlayer
import com.example.playlistmaker.presentation.ui.host.HostActivity
import com.example.playlistmaker.presentation.view_model.library.LibraryViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class FragmentEnterPlaylist : Fragment() {

    private var _binding: FragmentEnterPlaylistBinding? = null
    private val binding get() = _binding!!

    private var _bottomSheetBindingOnCreate: BottomSheetEnterPlaylistBinding? = null
    private val bottomSheetBindingOnCreate get() = _bottomSheetBindingOnCreate!!

    private var _bottomSheetBindingEdit: BottomSheetEnterPlaylistEditBinding? = null
    private val bottomSheetBindingEdit get() = _bottomSheetBindingEdit!!

    private lateinit var bottomSheetBehaviorOnCreate: BottomSheetBehavior<View>
    private lateinit var bottomSheetBehaviorEdit: BottomSheetBehavior<View>

    private val viewModel: LibraryViewModel by viewModel()
    private lateinit var adapter: PlaylistEnterOnAdapter
    private var trackTimeAndCount: String = ""
    private var playlistId: Long? = null
    private var isPlaylistShared = false

    private val externalNavigatorInteractor: ExternalNavigatorInteractor by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        playlistId = arguments?.getLong("playlistId") ?: 0L
        Log.d("FragmentEnterPlaylist", "Received Playlist ID: $playlistId")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEnterPlaylistBinding.inflate(inflater, container, false)
        _bottomSheetBindingOnCreate =
            BottomSheetEnterPlaylistBinding.bind(binding.bottomSheetEnterPlaylist.root)
        _bottomSheetBindingEdit =
            BottomSheetEnterPlaylistEditBinding.bind(binding.bottomSheetEnterPlaylistEdit.root)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setBottomNavigationVisibility(false)

        adapter = PlaylistEnterOnAdapter(
            trackList = mutableListOf(),
            onTrackClick = { track -> onTrackClicked(track) },
            onTrackLongClick = { track -> onTrackLongClicked(track) }
        )
        setupBottomSheets()
        setupRecyclerView()




        viewModel.loadPlaylist(playlistId ?: 0L)

        viewModel.currentPlaylist.observe(viewLifecycleOwner) { playlist ->
            Log.d("DEBUG_TRACKS", "Playlist received: $playlist")
            if (playlist == null) {
                Log.e("DEBUG_TRACKS", "Playlist is null after loading!")
                return@observe
            }
            updatePlaylistInfo(playlist)
            viewModel.loadTracksForPlaylist(playlist.id)
        }
        binding.backButton.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.enterPlaylistHamburgerButton.setOnClickListener {
            viewModel.currentPlaylist.value?.let { playlist ->
                updatePlaylistInfoBottomSheet(playlist)
                toggleEditBottomSheet()
            }
        }

        bottomSheetBindingEdit.deletePlaylistBottomSheet.setOnClickListener {
            showDeletePlaylistDialog()
        }
        binding.enterPlaylistShareButton.setOnClickListener {
            sharePlaylist()
        }
        bottomSheetBindingEdit.shareButtonEnterPlaylist.setOnClickListener {
            Log.d("ShareButton", "Share button clicked look =)")
            sharePlaylist()
        }


        bottomSheetBindingEdit.editPlaylist.setOnClickListener {
            val playlistId = viewModel.currentPlaylist.value!!.id
            val bundle = Bundle().apply {
                putLong("playlistId", playlistId)
            }
            findNavController().navigate(R.id.fragmentPlayListAdd, bundle)
        }



        viewModel.tracks.observe(viewLifecycleOwner) { tracks ->
            adapter.updateTracks(tracks)

            if (tracks.size > 0) {
                bottomSheetBindingOnCreate.recycleViewEnterPlaylist.visibility = View.VISIBLE
                bottomSheetBindingOnCreate.emptyTextViewEnterPlaylist.visibility = View.GONE
            } else {
                bottomSheetBindingOnCreate.recycleViewEnterPlaylist.visibility = View.GONE
                bottomSheetBindingOnCreate.emptyTextViewEnterPlaylist.visibility = View.VISIBLE
            }

            trackTimeAndCount = formatTrackTimeAndCount(tracks)
            binding.enterSumTimePlaylistAndCount.text = trackTimeAndCount
        }
        viewModel.currentPlaylist.observe(viewLifecycleOwner) { playlist ->
            viewModel.loadTracksForPlaylist(playlist!!.id)
        }
    }


    private fun formatTrackCount(tracks: List<Track>): String {
        return "${tracks.size} треков"
    }

    private fun updatePlaylistInfoBottomSheet(playlist: Playlist) {
        bottomSheetBindingEdit.playlistNameBottomSheet.text = playlist.name
        bottomSheetBindingEdit.playlistCountBottomSheet.text = formatTrackCount(playlist.tracks)

        val imagePath = playlist.imagePath
        Glide.with(bottomSheetBindingEdit.playlistImageBottomSheet.context)
            .load(imagePath)
            .apply(RequestOptions().transform(CenterCrop(), RoundedCorners(2)))
            .into(bottomSheetBindingEdit.playlistImageBottomSheet)
    }

    private fun sharePlaylist() {
        val playlist = viewModel.currentPlaylist.value
        if (playlist == null || playlist.tracks.isEmpty()) {
            Toast.makeText(requireContext(), R.string.emptyTrackSend, Toast.LENGTH_SHORT).show()
            return
        }

        val shareText = getPlaylistShareText()
        externalNavigatorInteractor.sharePlaylistApp(shareText)
        isPlaylistShared = true
    }


    override fun onPause() {
        super.onPause()
//        if (isPlaylistShared) {
//            findNavController().navigateUp()
//        }
    }





    private fun deletePlaylist() {
        val playlistId = playlistId ?: return
        viewModel.deletePlaylist(playlistId)
        findNavController().navigateUp()
    }

    private fun getPlaylistShareText(): String {
        val playlist = viewModel.currentPlaylist.value ?: return ""
        val tracks = viewModel.tracks.value ?: emptyList()
        val playlistInfo = "Плейлист: ${playlist.name}\n${playlist.description}\n\n"

        val trackCount = tracks.size
        val trackCountText = "Количество треков: [$trackCount]\n\n"
        val trackList = tracks.mapIndexed { index, track ->
            val trackDuration = formatTrackDuration(track.trackTimeMillis.toLong())
            "${index + 1}. ${track.artistName} - ${track.trackName} ($trackDuration)"
        }.joinToString("\n")

        return "$playlistInfo$trackCountText$trackList"
    }

    private fun formatTrackDuration(durationInMillis: Long): String {
        val minutes = (durationInMillis / 1000) / 60
        val seconds = (durationInMillis / 1000) % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    private fun setBottomNavigationVisibility(isVisible: Boolean) {
        (activity as? HostActivity)?.setBottomNavigationVisibility(isVisible)
    }

    private fun setupBottomSheets() {
        bottomSheetBehaviorOnCreate =
            BottomSheetBehavior.from(bottomSheetBindingOnCreate.root as View).apply {
                state = BottomSheetBehavior.STATE_EXPANDED
                isHideable = false
            }

        bottomSheetBehaviorEdit =
            BottomSheetBehavior.from(bottomSheetBindingEdit.root as View).apply {
                state = BottomSheetBehavior.STATE_HIDDEN
                addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                    override fun onStateChanged(bottomSheet: View, newState: Int) {
                        val overlay = binding.root.findViewById<View>(R.id.overlay_playlist_track)
                        when (newState) {
                            BottomSheetBehavior.STATE_EXPANDED -> {
                                overlay.visibility = View.VISIBLE
                            }

                            BottomSheetBehavior.STATE_HIDDEN, BottomSheetBehavior.STATE_COLLAPSED -> {
                                overlay.visibility = View.GONE
                            }
                        }
                    }

                    override fun onSlide(bottomSheet: View, slideOffset: Float) {
                        //TODO Not now , Valera!
                    }
                })
            }

        binding.root.findViewById<View>(R.id.overlay_playlist_track).setOnClickListener {
            bottomSheetBehaviorEdit.state = BottomSheetBehavior.STATE_HIDDEN
            binding.root.findViewById<View>(R.id.overlay_playlist_track).visibility = View.GONE
        }
    }

    private fun toggleEditBottomSheet() {
        val overlay = binding.root.findViewById<View>(R.id.overlay_playlist_track)
        if (bottomSheetBehaviorEdit.state == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehaviorEdit.state = BottomSheetBehavior.STATE_HIDDEN
            overlay.visibility = View.GONE
        } else {
            bottomSheetBehaviorEdit.state = BottomSheetBehavior.STATE_EXPANDED
            overlay.visibility = View.VISIBLE
        }
    }

    private fun setupRecyclerView() {
        val layoutManager = LinearLayoutManager(requireContext())
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        layoutManager.reverseLayout = false

        bottomSheetBindingOnCreate.recycleViewEnterPlaylist.layoutManager = layoutManager
        bottomSheetBindingOnCreate.recycleViewEnterPlaylist.adapter = adapter

    }


    private fun onTrackClicked(track: Track) {
        val bundle = Bundle().apply {
            putSerializable(FragmentPlayer.TRACK_DATA, track)
        }

        findNavController().navigate(
            R.id.action_fragmentEnterPlaylist_to_fragmentPlayer,
            bundle
        )
    }
    private fun onTrackLongClicked(track: Track) {
        showDeleteTrackDialog(track)
    }

    private fun updatePlaylistInfo(playlist: Playlist) {
        binding.enterPlaylistName.text = playlist.name ?: ""
        binding.enterDescriptionPlaylist.text = playlist.description ?: ""
        binding.enterSumTimePlaylistAndCount.text = trackTimeAndCount

        val imagePath = playlist.imagePath
        Glide.with(binding.enterImagePlaylist.context)
            .load(imagePath)
            .apply {
                if (imagePath.isNullOrEmpty()) {
                    centerInside()
                } else {
                    centerCrop()
                }
            }
            .into(binding.enterImagePlaylist)

        Log.d("PlaylistDebug", "Tracks: ${playlist.tracks}")
        Log.d("PlaylistDebug", "Track count: ${playlist.tracks.size}")
        Log.d("PlaylistDebug", "Total time millis: ${playlist.tracks.sumOf { it.trackTimeMillis }}")
    }

    private fun showDeleteTrackDialog(track: Track) {
        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle("Удалить трек?")
            .setMessage("Вы уверены, что хотите удалить трек из плейлиста?")
            .setNegativeButton("Нет") { dialog, _ -> dialog.dismiss() }
            .setNeutralButton("Да") { _, _ -> deleteTrackFromPlaylist(track) }
            .show()
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
            ?.setTextColor(ContextCompat.getColor(requireContext(), R.color.yp_blue))
        dialog.getButton(AlertDialog.BUTTON_NEUTRAL)
            ?.setTextColor(ContextCompat.getColor(requireContext(), R.color.yp_blue))
    }

    private fun showDeletePlaylistDialog() {
        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle("Удалить плейлист?")
            .setMessage("Вы уверены, что хотите удалить этот плейлист?")
            .setNegativeButton("Нет") { dialog, _ -> dialog.dismiss() }
            .setNeutralButton("Да") { _, _ -> deletePlaylist() }
            .show()

        dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
            ?.setTextColor(ContextCompat.getColor(requireContext(), R.color.yp_blue))
        dialog.getButton(AlertDialog.BUTTON_NEUTRAL)
            ?.setTextColor(ContextCompat.getColor(requireContext(), R.color.yp_blue))
    }

    private fun deleteTrackFromPlaylist(track: Track) {
        playlistId?.let { playlistId ->
            viewModel.removeTrackFromPlaylist(playlistId, track.trackId)
            viewModel.loadTracksForPlaylist(playlistId)
        }
    }

    private fun formatTrackTimeAndCount(tracks: List<Track>): String {
        val totalTimeMillis = tracks.sumOf { it.trackTimeMillis }
        val totalTimeSeconds = totalTimeMillis / 1000
        val minutes = totalTimeSeconds / 60
        val seconds = totalTimeSeconds % 60
        return String.format("%02d:%02d • %d треков", minutes, seconds, tracks.size)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        _bottomSheetBindingOnCreate = null
        (activity as? HostActivity)?.setBottomNavigationVisibility(true)
    }

    override fun onDestroy() {
        super.onDestroy()
        (activity as? HostActivity)?.setBottomNavigationVisibility(true)
    }

    override fun onResume() {
        (activity as? HostActivity)?.setBottomNavigationVisibility(false)
        super.onResume()
    }

    companion object {
        private const val PLAYLIST_ID_KEY = "playlist_id"
        private const val TRACK_DATA = "TRACK_DATA"

        fun newInstance(playlistId: Long): FragmentEnterPlaylist {
            return FragmentEnterPlaylist().apply {
                arguments = Bundle().apply {
                    putLong(PLAYLIST_ID_KEY, playlistId)
                }
            }
        }
    }
}
