package com.example.playlistmaker.presentation.fragments.media_library

import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.viewModels
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentTabLayoutLibraryBinding
import com.example.playlistmaker.presentation.view_model.library.LibraryViewModel
import com.google.android.material.tabs.TabLayoutMediator


class TabLayoutLibrary : Fragment() {

    private val viewModel: LibraryViewModel by viewModels()

    companion object {
        fun newInstance() = TabLayoutLibrary()
    }

    private lateinit var binding: FragmentTabLayoutLibraryBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTabLayoutLibraryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = TabLayoutViewPageAdapter(childFragmentManager, lifecycle)
        binding.viewPager.adapter = adapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->

            when (position) {

                0 -> {
                    tab.text = getString(R.string.favorit_track)
                    viewModel.toggleTab(true)
                }

                1 -> {
                    tab.text = getString(R.string.play_lists)
                    viewModel.toggleTab(false)
                }
            }
        }.attach()
    }
}
