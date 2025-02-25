package com.example.playlistmaker.presentation.fragments.media_library

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.databinding.FragmentTracksListLibraryBinding
import com.example.playlistmaker.presentation.view_model.library.LibraryViewModel
import com.example.playlistmaker.presentation.fragments.search.TrackAdapter
import com.example.playlistmaker.presentation.ui.player.PlayerActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel



class FavoriteTracksListLibraryFragment : Fragment() {

    private val libraryViewModel: LibraryViewModel by viewModel()
    private lateinit var binding: FragmentTracksListLibraryBinding
    private lateinit var trackAdapter: TrackAdapter

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
        trackAdapter = TrackAdapter(onTrackClick = { track ->
            libraryViewModel.trackClicked(track)
        })
        binding.recyclerViewLibrary.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = trackAdapter
        }
    }

    private fun observeViewModel() {
        libraryViewModel.selectedTrack.observe(viewLifecycleOwner) { track ->
            track?.let {
                if (isClickable) {
                    isClickable = false
                    val intent = Intent(requireContext(), PlayerActivity::class.java).apply {
                        putExtra(PlayerActivity.TRACK_DATA, it)
                    }
                    startActivity(intent)


                    lifecycleScope.launch {
                        delay(CLICK_DEBOUNCE_DELAY)
                        isClickable = true
                    }
                }
            }
        }

        libraryViewModel.tracks.observe(viewLifecycleOwner) { tracks ->
            if (tracks.isEmpty()) {
                binding.noOneTrackOnLibrary.visibility = View.VISIBLE
                binding.errorPlaceHolderImg.visibility = View.VISIBLE
                binding.errorPlaceHolderText.visibility = View.VISIBLE
                binding.recyclerViewLibrary.visibility = View.GONE
            } else {
                binding.noOneTrackOnLibrary.visibility = View.GONE
                binding.recyclerViewLibrary.visibility = View.VISIBLE
                trackAdapter.updateData(tracks)
            }
        }

        libraryViewModel.isEmpty.observe(viewLifecycleOwner) { isEmpty ->
            binding.noOneTrackOnLibrary.visibility = if (isEmpty) View.VISIBLE else View.GONE
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


