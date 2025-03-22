package com.example.playlistmaker.presentation.fragments.media_library.playlist.playlist_fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistsBinding
import com.example.playlistmaker.presentation.ui.host.HostActivity
import com.example.playlistmaker.presentation.view_model.library.LibraryViewModel
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
        val spacing = resources.getDimensionPixelSize(R.dimen.card_margin_horizontal8dp)

        playlistAdapter = PlaylistAdapter { playlistId ->
            onPlaylistClicked(playlistId)
        }

        binding.recycleViewPlayList.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.recycleViewPlayList.adapter = playlistAdapter
        binding.recycleViewPlayList.addItemDecoration(HorizontalSpacingItemDecoration(spacing))
    }

    private fun onPlaylistClicked(playlistId: Long) {
        val bundle = bundleOf("playlistId" to playlistId)
        (activity as? HostActivity)?.setBottomNavigationVisibility(false)
        findNavController().navigate(R.id.fragmentEnterPlaylist, bundle)
    }




    @SuppressLint("NotifyDataSetChanged")
    private fun observePlaylists() {
        libraryViewModel.playlists.observe(viewLifecycleOwner) { playlists ->
            val hasPlaylists = playlists.isNotEmpty()

            if (hasPlaylists) {
                binding.recycleViewPlayList.visibility = View.VISIBLE
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