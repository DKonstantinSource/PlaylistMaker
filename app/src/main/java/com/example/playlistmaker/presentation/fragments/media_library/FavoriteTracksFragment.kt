package com.example.playlistmaker.presentation.fragments.media_library

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.playlistmaker.databinding.FragmentFavoriteTracksBinding
import com.example.playlistmaker.presentation.view_model.library.LibraryViewModel

class FavoriteTracksFragment : Fragment() {

    private val viewModel: LibraryViewModel by viewModels()


    companion object {
        private const val NUMBER = "number"

        fun newInstance(number: Int) = FavoriteTracksFragment().apply {
            arguments = Bundle().apply {
                putInt(NUMBER, number)
            }
        }
    }

    private lateinit var binding: FragmentFavoriteTracksBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavoriteTracksBinding.inflate(inflater, container, false)

        binding.noOneTrackOnLibrary.visibility = View.VISIBLE

        return binding.root
    }

}