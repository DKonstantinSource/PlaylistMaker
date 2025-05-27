//package com.example.playlistmaker.presentation.fragments.media_library.tab_layout
//
//import androidx.fragment.app.Fragment
//import androidx.fragment.app.FragmentManager
//import androidx.lifecycle.Lifecycle
//import androidx.viewpager2.adapter.FragmentStateAdapter
//import com.example.playlistmaker.presentation.fragments.media_library.favorit_track.FavoriteTracksListLibraryFragment
//import com.example.playlistmaker.presentation.fragments.media_library.playlist.playlist_fragment.FragmentPlaylist
//
//class TabLayoutViewPageAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
//    FragmentStateAdapter(fragmentManager, lifecycle) {
//
//    override fun getItemCount(): Int {
//        return 2
//    }
//
//    override fun createFragment(position: Int): Fragment {
//        return when (position) {
//            0 -> FavoriteTracksListLibraryFragment.newInstance(1)
//            1 -> FragmentPlaylist.newInstance(2)
//            else -> throw IllegalStateException("Unexpected position: $position")
//        }
//    }
//}