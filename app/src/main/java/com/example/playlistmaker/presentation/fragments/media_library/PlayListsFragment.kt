package com.example.playlistmaker.presentation.fragments.media_library

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.playlistmaker.databinding.FragmentPlaylistsBinding
import com.example.playlistmaker.presentation.view_model.library.LibraryViewModel

class PlayListsFragment : Fragment() {

    private val viewModel: LibraryViewModel by viewModels()

    companion object {
        private const val NUMBER = "number"

        fun newInstance(number: Int) = PlayListsFragment().apply {
            arguments = Bundle().apply {
                putInt(NUMBER, number)
            }
        }
    }

    private lateinit var binding: FragmentPlaylistsBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentPlaylistsBinding.inflate(inflater, container, false)

        binding.noOnePlayLists.visibility = View.VISIBLE
        binding.newPlayList.visibility = View.VISIBLE
        binding.emptyPlayListText.visibility = View.VISIBLE
        binding.emptyPlayListImage.visibility = View.VISIBLE

//        viewModel.tracks.observe(viewLifecycleOwner) { tracks ->
//            binding.newPlayList.visibility = View.VISIBLE
//            binding.emptyPlayListText.visibility = View.VISIBLE
//            binding.emptyPlayListImage.visibility = if (tracks.isEmpty()) {
//                View.VISIBLE
//            } else {
//                View.VISIBLE
//            }
//            // TODO Так тут потом реализую логику отображения трэков
//        }

        return binding.root
    }
}