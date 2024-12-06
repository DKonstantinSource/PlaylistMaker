package com.example.playlistmaker.presentation.ui.search

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.preference.PreferenceManager
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.Constants.CLICK_DEBOUNCE_DELAY
import com.example.playlistmaker.Constants.SEARCH_DEBOUNCE_DELAY
import com.example.playlistmaker.Creator.Creator
import com.example.playlistmaker.presentation.ui.player.PlayerActivity
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.use_case.ManageSearchHistoryUseCase
import com.example.playlistmaker.domain.use_case.SearchTracksUseCase


class SearchActivity : AppCompatActivity() {
    private lateinit var searchEditText: EditText
    private lateinit var resetButton: Button
    private lateinit var refreshButton: Button
    private var searchQuery: String = ""
    private var lastQuery: String? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var trackAdapter: TrackAdapter
    private lateinit var errorSearchNothing: View
    private lateinit var errorConnectionPlaceHolder: View
    private lateinit var refreshHistoryButton: Button
    private lateinit var hiddenText: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var searchTracksUseCase: SearchTracksUseCase
    private lateinit var manageSearchHistoryUseCase: ManageSearchHistoryUseCase


    private val handler = Handler(Looper.getMainLooper())


    @SuppressLint("ResourceType", "MissingInflatedId", "CutPasteId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        var isClickable = true
        val handler = Handler(Looper.getMainLooper())
        searchTracksUseCase = Creator.createSearchTracksUseCase()


        setContentView(R.layout.activity_search)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        searchEditText = findViewById(R.id.searchEditText)
        searchEditText.setText(searchQuery)
        resetButton = findViewById(R.id.reset_button)
        errorSearchNothing = findViewById(R.id.errorSearchNothing)
        errorConnectionPlaceHolder = findViewById(R.id.errorConnectionPlaceHolder)
        refreshButton = findViewById(R.id.refreshButton)
        refreshHistoryButton = findViewById(R.id.clearHistoryButton)
        hiddenText = findViewById(R.id.prevSearch)
        progressBar = findViewById(R.id.progressBar)


        val backOnMainActivity = findViewById<ImageView>(R.id.backButton)
        backOnMainActivity.setOnClickListener { finish() }

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        manageSearchHistoryUseCase = Creator.createManageSearchHistoryUseCase(sharedPreferences)
        val history = manageSearchHistoryUseCase.getSearchHistory()

        trackAdapter = TrackAdapter { track ->
            manageSearchHistoryUseCase.addToHistory(track)
            if (searchQuery.isEmpty() or (searchQuery == "")) {
                trackAdapter.updateData(manageSearchHistoryUseCase.getSearchHistory())
            }

            val intent = Intent(this, PlayerActivity::class.java).apply {
                putExtra(TRACK_DATA, track)
            }
            if (isClickable) {
                isClickable = false
                startActivity(intent)
                handler.postDelayed({ isClickable = true }, CLICK_DEBOUNCE_DELAY)
            }

        }




        resetButton.setOnClickListener {
            searchEditText.text.clear()
            searchQuery = ""
            searchEditText.clearFocus()
            val tracksHistory = manageSearchHistoryUseCase.getSearchHistory()

            if (tracksHistory.isNotEmpty()) {
                trackAdapter.updateData(tracksHistory)
                recyclerView.adapter = trackAdapter
                hiddenText.visibility = View.VISIBLE
                refreshHistoryButton.visibility = View.VISIBLE
                resetButton.visibility = View.GONE
            }

        }


        refreshButton.setOnClickListener {
            lastQuery?.let { query ->
                searchTracksUseCase.execute(query) { tracks ->
                    runOnUiThread {
                        if (tracks.isNullOrEmpty()) {
                            errorSearchNothing.visibility = View.VISIBLE
                            recyclerView.visibility = View.GONE
                        } else {
                            errorSearchNothing.visibility = View.GONE
                            trackAdapter.updateData(tracks)
                            recyclerView.adapter = trackAdapter
                            recyclerView.visibility = View.VISIBLE
                        }
                    }
                }
            }
        }



        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                refreshButton.visibility = View.GONE
                val query = searchEditText.text.toString()
                if (query.isNotEmpty()) {
                    resetButton.visibility = View.VISIBLE
                } else {
                    resetButton.visibility = View.GONE
                }


                searchTracksUseCase.execute(query) { tracks ->
                    runOnUiThread {
                        if (tracks.isNullOrEmpty()) {
                            resetButton.visibility = View.GONE

                            val result = manageSearchHistoryUseCase.getSearchHistory()
                            trackAdapter.updateData(result)
                            if (result.isNullOrEmpty() and query.isEmpty()) {
                                recyclerView.visibility = View.GONE
                                hiddenText.visibility = View.GONE
                                refreshHistoryButton.visibility = View.GONE
                            } else {
                                recyclerView.visibility = View.VISIBLE
                                hiddenText.visibility = View.VISIBLE
                                refreshHistoryButton.visibility = View.VISIBLE
                            }
                            recyclerView.adapter = trackAdapter
                            recyclerView.visibility = View.VISIBLE
                            hiddenText.visibility = View.VISIBLE
                            refreshHistoryButton.visibility = View.VISIBLE
                            errorSearchNothing.visibility = View.GONE
                            if (query.isBlank() and result.isEmpty()) {
                                resetButton.visibility = View.GONE
                                hiddenText.visibility = View.GONE
                                refreshHistoryButton.visibility = View.GONE
                            }
                        } else {
                            if (tracks.isNullOrEmpty() and query.isNotEmpty()) {
                                errorSearchNothing.visibility = View.VISIBLE
                            } else {
                                errorSearchNothing.visibility = View.GONE
                                trackAdapter.updateData(tracks)
                                recyclerView.adapter = trackAdapter
                                recyclerView.visibility = View.VISIBLE
                                hiddenText.visibility = View.VISIBLE
                                refreshHistoryButton.visibility = View.VISIBLE

                            }
                        }
                    }
                }
                true
            } else {
                false
            }
        }
        searchEditText = findViewById(R.id.searchEditText)
        recyclerView = findViewById(R.id.tracks)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = trackAdapter

        if (manageSearchHistoryUseCase.getSearchHistory().isNotEmpty()) {
            hiddenText.visibility = View.VISIBLE
            refreshHistoryButton.visibility = View.VISIBLE
            trackAdapter.updateData(manageSearchHistoryUseCase.getSearchHistory())
        } else {
            hiddenText.visibility = View.GONE
            refreshHistoryButton.visibility = View.GONE

        }

        refreshHistoryButton.setOnClickListener {
            manageSearchHistoryUseCase.clearHistory()
            recyclerView.visibility = View.GONE
            hiddenText.visibility = View.GONE
            refreshHistoryButton.visibility = View.GONE
        }

        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchQuery = s.toString()
                resetButton.visibility =
                    if (searchQuery.trim().isNotEmpty()) View.VISIBLE else View.GONE

                if (searchQuery.isBlank()) {
                    errorSearchNothing.visibility = View.GONE
                    val tracks = manageSearchHistoryUseCase.getSearchHistory()
                    if (!tracks.isNullOrEmpty()) {
                        trackAdapter.updateData(tracks)
                        recyclerView.adapter = trackAdapter
                        recyclerView.visibility = View.VISIBLE
                        hiddenText.visibility = View.VISIBLE
                        refreshHistoryButton.visibility = View.VISIBLE
                        errorSearchNothing.visibility = View.GONE
                    } else {
                        errorSearchNothing.visibility = View.GONE
                    }
                    resetButton.visibility = View.GONE
                } else {
                    hiddenText.visibility = View.GONE
                    recyclerView.visibility = View.GONE
                    refreshHistoryButton.visibility = View.GONE
                    resetButton.visibility = View.VISIBLE
                    searchDebounce()
                }
            }
        })

    }

    private val searchRunnable = Runnable {
        searchTracksUseCase = Creator.createSearchTracksUseCase()
        searchTracksUseCase.execute(searchQuery) { tracks ->
            runOnUiThread {
                if (tracks.isNullOrEmpty()) {
                    Log.d("UserSearch", "No results found for: $searchQuery")
                    if (searchQuery.isNotEmpty()) {
                        errorSearchNothing.visibility = View.VISIBLE
                    }
                } else {
                    trackAdapter.updateData(tracks)
                    recyclerView.adapter = trackAdapter
                    recyclerView.visibility = View.VISIBLE
                    errorSearchNothing.visibility = View.GONE
                }
            }
        }
    }


    private fun searchDebounce() {

        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    override fun onDestroy() {
        super.onDestroy()
        searchTracksUseCase.shutdown()
    }


    companion object {
        const val TRACK_DATA = "TRACK_DATA"
        const val SEARCH_KEY_ON_STATE = "search_query"

    }
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_KEY_ON_STATE, searchQuery)
    }

}