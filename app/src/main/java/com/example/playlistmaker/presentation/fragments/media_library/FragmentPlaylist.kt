package com.example.playlistmaker.presentation.fragments.media_library

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.playlistmaker.databinding.FragmentPlaylistsBinding
import com.example.playlistmaker.presentation.view_model.library.LibraryViewModel
import com.google.android.material.tabs.TabLayoutMediator
import org.koin.androidx.viewmodel.ext.android.viewModel

class FragmentPlaylist : Fragment() {

    private var _binding: FragmentPlaylistsBinding? = null
    private val binding get() = _binding!!
    private val libraryViewModel: LibraryViewModel by viewModel()
    private lateinit var tabMediator: TabLayoutMediator

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistsBinding.inflate(inflater, container, false)


        setupUI()
        observeViewModel()

        return binding.root
    }


    private fun setupUI() {
        binding.noOnePlayLists.visibility = View.VISIBLE
        binding.newPlayList.visibility = View.VISIBLE
        binding.emptyPlayListText.visibility = View.VISIBLE
        binding.emptyPlayListImage.visibility = View.VISIBLE
    }

    private fun observeViewModel() {
        libraryViewModel.tracks.observe(viewLifecycleOwner) { tracks ->
            if (tracks.isEmpty()) {
                binding.emptyPlayListImage.visibility = View.VISIBLE
                binding.emptyPlayListText.visibility = View.VISIBLE
            } else {
                binding.emptyPlayListImage.visibility = View.GONE
                binding.emptyPlayListText.visibility = View.GONE
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        if (::tabMediator.isInitialized) {
            tabMediator.detach()
        }
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