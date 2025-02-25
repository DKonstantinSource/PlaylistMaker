package com.example.playlistmaker.presentation.fragments.media_library

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.databinding.FragmentTracksListLibraryBinding
import com.example.playlistmaker.presentation.view_model.library.LibraryViewModel
import com.example.playlistmaker.presentation.fragments.search.TrackAdapter
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.presentation.ui.player.PlayerActivity
import org.koin.androidx.viewmodel.ext.android.viewModel


class FavoriteTracksListLibraryFragment : Fragment() {

    private val libraryViewModel: LibraryViewModel by viewModel()
    private lateinit var binding: FragmentTracksListLibraryBinding
    private lateinit var trackAdapter: TrackAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTracksListLibraryBinding.inflate(inflater, container, false)
        libraryViewModel.loadFavoriteTracks()
        observeViewModel()
        setupRecyclerView()
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val position = arguments?.getInt("TAB_POSITION") ?: 0
    }

    private fun setupRecyclerView() {
        trackAdapter = TrackAdapter(onTrackClick = { track -> openAudioPlayer(track) })
        binding.recyclerViewLibrary.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = trackAdapter
        }
    }

    private fun observeViewModel() {
        libraryViewModel.tracks.observe(viewLifecycleOwner) { tracks ->
            if (tracks.isEmpty()) {
                binding.noOneTrackOnLibrary.visibility = View.VISIBLE
                binding.recyclerViewLibrary.visibility = View.GONE
            } else {
                binding.noOneTrackOnLibrary.visibility = View.GONE
                binding.recyclerViewLibrary.visibility = View.VISIBLE
                trackAdapter.updateData(tracks)
            }
        }

        libraryViewModel.isEmpty.observe(viewLifecycleOwner) { isEmpty ->
            if (isEmpty) {
                binding.noOneTrackOnLibrary.visibility = View.VISIBLE
            } else {
                binding.noOneTrackOnLibrary.visibility = View.GONE
            }
        }
    }

    private fun openAudioPlayer(track: Track) {
        val intent = Intent(requireContext(), PlayerActivity::class.java).apply {
            putExtra("TRACK_KEY", track)
        }
        startActivity(intent)
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

