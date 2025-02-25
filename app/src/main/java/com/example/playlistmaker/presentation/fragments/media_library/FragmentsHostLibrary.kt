package com.example.playlistmaker.presentation.fragments.media_library

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentLibraryTitleBinding
import com.example.playlistmaker.presentation.view_model.library.LibraryViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class FragmentsHostLibrary : Fragment() {

    private val libraryViewModel: LibraryViewModel by viewModel()

    companion object {
        fun newInstance() = FragmentsHostLibrary()
    }

    private lateinit var binding: FragmentLibraryTitleBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLibraryTitleBinding.inflate(inflater, container, false)

        childFragmentManager.beginTransaction()
            .add(R.id.fragment_title_child_container, FragmentTabLayoutLibrary.newInstance())
            .commit()


        return binding.root
    }
}