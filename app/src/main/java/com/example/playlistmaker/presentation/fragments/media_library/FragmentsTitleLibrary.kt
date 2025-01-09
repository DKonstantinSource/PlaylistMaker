package com.example.playlistmaker.presentation.fragments.media_library

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentLibraryTitleBinding
import com.example.playlistmaker.presentation.view_model.library.LibraryViewModel

class FragmentsTitleLibrary : Fragment() {

    private val viewModel: LibraryViewModel by viewModels()

    companion object {
        fun newInstance() = FragmentsTitleLibrary()
    }

    private lateinit var binding: FragmentLibraryTitleBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLibraryTitleBinding.inflate(inflater, container, false)

        childFragmentManager.beginTransaction()
            .add(R.id.fragment_title_child_container, TabLayoutLibrary.newInstance())
            .addToBackStack(null)
            .commit()

        binding.backButton.setOnClickListener {
            requireActivity().finish()
        }

        return binding.root
    }
}