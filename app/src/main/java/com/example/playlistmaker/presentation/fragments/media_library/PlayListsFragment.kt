package com.example.playlistmaker.presentation.fragments.media_library

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.playlistmaker.databinding.FragmentPlaylistsBinding
import com.example.playlistmaker.presentation.view_model.library.LibraryViewModel
import com.google.android.material.tabs.TabLayoutMediator

class PlayListsFragment : Fragment() {

    private lateinit var binding: FragmentPlaylistsBinding
    private val viewModel: LibraryViewModel by viewModels()
    private lateinit var tabMediator: TabLayoutMediator

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlaylistsBinding.inflate(inflater, container, false)

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
        viewModel.tracks.observe(viewLifecycleOwner) { tracks ->
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
        if (::tabMediator.isInitialized) {
            tabMediator.detach()
        }
    }

    companion object {
        private const val NUMBER = "number"

        fun newInstance(number: Int) = PlayListsFragment().apply {
            arguments = Bundle().apply {
                putInt(NUMBER, number)
            }
        }
    }
}