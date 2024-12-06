package com.example.playlistmaker.data.impl

import android.content.SharedPreferences
import com.example.playlistmaker.Constants
import com.example.playlistmaker.domain.repository.SearchHistoryRepository
import com.example.playlistmaker.domain.model.Track
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SearchHistory(private val sharedPreferences: SharedPreferences) : SearchHistoryRepository {
    private val historyKey = Constants.SEARCH_HSITORY
    private val maxHistorySize = 10
    private val gson = Gson()

    override fun getSearchHistory(): List<Track> {
        val json = sharedPreferences.getString(historyKey, null)
        return if (json != null) {
            val type = object : TypeToken<List<Track>>() {}.type
            gson.fromJson(json, type)
        } else {
            emptyList()
        }
    }

    override fun addToHistory(track: Track) {
        val history = getSearchHistory().toMutableList()
        history.removeAll { it.trackId == track.trackId }
        history.add(0, track)
        if (history.size > maxHistorySize) {
            history.removeAt(maxHistorySize)
        }
        saveHistory(history)
    }

    override fun clearHistory() {
        sharedPreferences.edit().remove(historyKey).apply()
    }

    private fun saveHistory(history: List<Track>) {
        val json = gson.toJson(history)
        sharedPreferences.edit().putString(historyKey, json).apply()
    }
}