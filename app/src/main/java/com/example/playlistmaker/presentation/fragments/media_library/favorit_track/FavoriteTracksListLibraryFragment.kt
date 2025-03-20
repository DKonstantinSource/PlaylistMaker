package com.example.playlistmaker.presentation.fragments.media_library.favorit_track

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentTracksListLibraryBinding
import com.example.playlistmaker.presentation.fragments.media_library.favorit_track.adapter.FavoriteTrackAdapter
import com.example.playlistmaker.presentation.fragments.player.FragmentPlayer
import com.example.playlistmaker.presentation.view_model.library.LibraryViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoriteTracksListLibraryFragment : Fragment() {

    private val libraryViewModel: LibraryViewModel by viewModel()
    private lateinit var binding: FragmentTracksListLibraryBinding
    private lateinit var favoriteTrackAdapter: FavoriteTrackAdapter

    private var isClickable = true
    private val CLICK_DEBOUNCE_DELAY = 500L

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTracksListLibraryBinding.inflate(inflater, container, false)

        setupRecyclerView()
        observeViewModel()
        libraryViewModel.loadFavoriteTracks()

        return binding.root
    }

    private fun setupRecyclerView() {
        favoriteTrackAdapter = FavoriteTrackAdapter(onTrackClick = { track ->
            libraryViewModel.trackClicked(track)
        })
        binding.recyclerViewLibrary.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = favoriteTrackAdapter
        }
    }

    private fun observeViewModel() {
        libraryViewModel.selectedTrack.observe(viewLifecycleOwner) { track ->
            track?.let {
                if (isClickable) {
                    isClickable = false

                    val bundle = Bundle().apply {
                        putSerializable(FragmentPlayer.TRACK_DATA, it)
                    }

                    findNavController().navigate(
                        R.id.action_libraryTitleFragment_to_fragmentPlayer,
                        bundle
                    )

                    lifecycleScope.launch {
                        delay(CLICK_DEBOUNCE_DELAY)
                        isClickable = true
                    }
                }
            }
        }

        libraryViewModel.tracks.observe(viewLifecycleOwner) { tracks ->
            val isEmpty = tracks.isEmpty()

            binding.noOneTrackOnLibrary.visibility = if (isEmpty) View.VISIBLE else View.GONE
            binding.errorPlaceHolderImg.visibility = if (isEmpty) View.VISIBLE else View.GONE
            binding.errorPlaceHolderText.visibility = if (isEmpty) View.VISIBLE else View.GONE
            binding.recyclerViewLibrary.visibility = if (isEmpty) View.GONE else View.VISIBLE

            if (!isEmpty) {
                favoriteTrackAdapter.updateData(tracks)
            }
        }
    }


    override fun onPause() {
        super.onPause()
        libraryViewModel.clearSelectedTrack()
    }

    companion object {
        fun newInstance(position: Int): FavoriteTracksListLibraryFragment {
            return FavoriteTracksListLibraryFragment().apply {
                arguments = Bundle().apply {
                    putInt("TAB_POSITION", position)
                }
            }
        }
    }
}
