package com.example.playlistmaker.presentation.fragments.media_library.playlist.playlist_fragment

import android.annotation.SuppressLint
import android.content.res.Resources
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistsBinding
import com.example.playlistmaker.presentation.view_model.library.LibraryViewModel
import com.google.android.material.snackbar.Snackbar
import org.koin.androidx.viewmodel.ext.android.viewModel

class FragmentPlaylist : Fragment() {

    private var _binding: FragmentPlaylistsBinding? = null
    private val binding get() = _binding!!
    private val libraryViewModel: LibraryViewModel by viewModel()

    private lateinit var playlistAdapter: PlaylistAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistsBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observePlaylists()

        binding.buttonNewList.setOnClickListener {
            findNavController().navigate(R.id.fragmentPlayListAdd)
        }


    }


    private fun setupRecyclerView() {
        playlistAdapter = PlaylistAdapter()
        binding.recycleViewPlayList.apply {
            layoutManager = GridLayoutManager(context, 2)
            addItemDecoration(PlaylistItemDecoration(16, 8))
            adapter = playlistAdapter
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun observePlaylists() {
        libraryViewModel.playlists.observe(viewLifecycleOwner) { playlists ->
            val hasPlaylists = playlists.isNotEmpty()

            if (hasPlaylists) {
                binding.recycleList.visibility = View.VISIBLE
                binding.buttonNewList.visibility = View.VISIBLE

                binding.emptyPlayListImage.visibility = View.GONE
                binding.emptyPlayListText.visibility = View.GONE
            } else {
                binding.emptyPlayListImage.visibility = View.VISIBLE
                binding.emptyPlayListText.visibility = View.VISIBLE
            }

            playlistAdapter.submitList(ArrayList(playlists))
            playlistAdapter.notifyDataSetChanged()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        libraryViewModel.loadPlaylists()
    }


    companion object {
        fun newInstance(position: Int): FragmentPlaylist {
            return FragmentPlaylist().apply {
                arguments = Bundle().apply {
                    putInt("TAB_POSITION", position)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val position = arguments?.getInt("TAB_POSITION") ?: 0
    }
}