package com.example.playlistmaker.domain.model


data class PlayerControl(
    var currentState: PlayerState = PlayerState.PREPARED
) {
    fun play() {
        currentState = PlayerState.PLAYING
    }

    fun pause() {
        currentState = PlayerState.PAUSED
    }

    fun stop() {
        currentState = PlayerState.PAUSED
    }

    fun isPlaying(): Boolean {
        return currentState == PlayerState.PLAYING
    }

}