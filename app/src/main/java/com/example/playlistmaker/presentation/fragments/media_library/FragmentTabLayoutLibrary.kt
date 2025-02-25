package com.example.playlistmaker.presentation.fragments.media_library

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentTabLayoutLibraryBinding
import com.example.playlistmaker.presentation.view_model.library.LibraryViewModel
import com.google.android.material.tabs.TabLayoutMediator
import org.koin.androidx.viewmodel.ext.android.viewModel


class FragmentTabLayoutLibrary : Fragment() {

    private val libraryViewModel: LibraryViewModel by viewModel()



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
                    libraryViewModel.toggleTab(true)
                }
                1 -> {
                    tab.text = getString(R.string.play_lists)
                    libraryViewModel.toggleTab(false)
                }
            }
        }.attach()

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
            }
        })

    }


    companion object {
        fun newInstance() = FragmentTabLayoutLibrary()
    }
}
