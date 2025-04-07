package com.example.playlistmaker.presentation.custom_view

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatImageView
import com.example.playlistmaker.R

class PlaybackButtonView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {

    private var isPlaying = false
    private var playIcon: Drawable? = null
    private var pauseIcon: Drawable? = null

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.PlaybackButtonView)
        playIcon = typedArray.getDrawable(R.styleable.PlaybackButtonView_playIcon)
        pauseIcon = typedArray.getDrawable(R.styleable.PlaybackButtonView_pauseIcon)
        setImageDrawable(playIcon)
        typedArray.recycle()

        isClickable = true
        isFocusable = true
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event?.action == MotionEvent.ACTION_UP) {
            toggle()
            performClick()
        }
        return true
    }

    override fun performClick(): Boolean {
        super.performClick()
        return true
    }

    fun toggle() {
        isPlaying = !isPlaying
        updateIcon()
    }

    fun setPlaying(isPlaying: Boolean) {
        this.isPlaying = isPlaying
        updateIcon()
    }

    private fun updateIcon() {
        setImageDrawable(if (isPlaying) pauseIcon else playIcon)
    }
}
