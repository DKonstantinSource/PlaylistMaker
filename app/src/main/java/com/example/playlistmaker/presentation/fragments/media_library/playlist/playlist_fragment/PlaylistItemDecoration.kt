package com.example.playlistmaker.presentation.fragments.media_library.playlist.playlist_fragment

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class PlaylistItemDecoration(private val sidePadding: Int, private val middlePadding: Int) :
    RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
        val column = position % 2

        outRect.top = middlePadding
        if (column == 0) {
            outRect.left = sidePadding
            outRect.right = middlePadding / 2
        } else {
            outRect.right = sidePadding
            outRect.left = middlePadding / 2
        }
    }
}
