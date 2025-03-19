package com.example.playlistmaker.presentation.fragments.media_library.playlist.playlist_fragment

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class HorizontalSpacingItemDecoration(private val spacing: Int) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
        val spanCount = 2

        if ((position + 1) % spanCount != 0) {
            outRect.right = spacing
        }
    }
}
