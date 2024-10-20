package com.example.playlistmaker

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SearchHistory(private val sharedPreferences: SharedPreferences) {
    private val historyKey = "search_history"
    private val maxHistorySize = 10
    private val gson = Gson()

    fun getSearchHistory(): List<Track> {
        val json = sharedPreferences.getString(historyKey, null)
        return if (json != null) {
            val type = object : TypeToken<List<Track>>() {}.type
            gson.fromJson(json, type)
        } else {
            emptyList()
        }
    }

    fun addToHistory(track: Track) {
        val history = getSearchHistory().toMutableList()
        history.removeAll { it.trackId == track.trackId }
        history.add(0, track)
        if (history.size > maxHistorySize) {
            history.removeAt(maxHistorySize)
        }
        saveHistory(history)


    }

    fun clearHistory() {
        sharedPreferences
            .edit()
            .remove(historyKey)
            .apply()
    }

    private fun saveHistory(history: List<Track>) {
        val json = gson.toJson(history)
        sharedPreferences
            .edit().putString(historyKey, json)
            .apply()
    }



}