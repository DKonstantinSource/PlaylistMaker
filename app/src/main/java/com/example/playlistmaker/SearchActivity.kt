package com.example.playlistmaker

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

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
    private lateinit var searchHistory: SearchHistory


    @SuppressLint("ResourceType", "MissingInflatedId", "CutPasteId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

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

        val sharedPreferences = getSharedPreferences(SEARCH_HSITORY, MODE_PRIVATE)
        searchHistory = SearchHistory(sharedPreferences)

        val backOnMainActivity = findViewById<ImageView>(R.id.backButton)
        backOnMainActivity.setOnClickListener { finish() }

        var historyTracks = searchHistory.getSearchHistory()


        trackAdapter = TrackAdapter { track ->
            searchHistory.addToHistory(track)
            if (searchQuery.isEmpty() or (searchQuery == "")) {
                trackAdapter.updateData(searchHistory.getSearchHistory())
            }

            val intent = Intent(this, PlayerActivity::class.java).apply {
                putExtra(TRACK_DATA, track)
            }
            startActivity(intent)
        }




        resetButton.setOnClickListener {
            searchEditText.text.clear()
            searchQuery = ""
            searchEditText.clearFocus()
            historyTracks = searchHistory.getSearchHistory()

            if (historyTracks.isNotEmpty()) {
                trackAdapter.updateData(historyTracks)
                recyclerView.adapter = trackAdapter
                hiddenText.visibility = View.VISIBLE
                refreshHistoryButton.visibility = View.VISIBLE
            }

        }


        refreshButton.setOnClickListener {
            lastQuery?.let { query ->
                errorConnectionPlaceHolder.visibility = View.GONE
                performSearch(query) { trackFound ->
                    if (trackFound) {
                        errorSearchNothing.visibility = View.GONE
                    } else {
                        errorSearchNothing.visibility = View.VISIBLE
                    }
                }
            }
        }



        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                refreshButton.visibility = View.GONE
                performSearch(searchQuery) { trackFound ->
                    if (trackFound) {
                        errorSearchNothing.visibility = View.GONE
                        refreshButton.visibility = View.GONE


                    } else {
                        errorSearchNothing.visibility = View.VISIBLE
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

        if (searchHistory.getSearchHistory().isNotEmpty()) {
            hiddenText.visibility = View.VISIBLE
            refreshHistoryButton.visibility = View.VISIBLE
            trackAdapter.updateData(searchHistory.getSearchHistory())
        } else {
            hiddenText.visibility = View.GONE
            refreshHistoryButton.visibility = View.GONE
        }


        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val query = searchEditText.text.toString().trim()

                if (query.isNotEmpty()) {
                    performSearch(query) { trackFound ->
                        if (trackFound) {
                            searchQuery = query.trim()
                            recyclerView.adapter = trackAdapter
                            errorSearchNothing.visibility = View.GONE
                        } else {
                            errorSearchNothing.visibility = View.VISIBLE
                        }
                    }
                }
                true
            } else {
                false
            }
        }


        refreshHistoryButton.setOnClickListener {
            searchHistory.clearHistory()
            recyclerView.visibility = View.GONE
            hiddenText.visibility = View.GONE
            refreshHistoryButton.visibility = View.GONE
        }

        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchQuery = s.toString()
                if (count != 0 && (searchQuery.isNotEmpty())) {


                    hiddenText.visibility = View.GONE
                    refreshHistoryButton.visibility = View.GONE
                    resetButton.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
                } else {

                    if (searchHistory.getSearchHistory().isNotEmpty()) {
                        resetButton.visibility = View.GONE
                        trackAdapter.updateData(searchHistory.getSearchHistory())
                        recyclerView.adapter = trackAdapter
                        recyclerView.visibility = View.VISIBLE
                        hiddenText.visibility = View.VISIBLE
                        refreshHistoryButton.visibility = View.VISIBLE
                    } else {
                        resetButton.visibility = View.GONE
                    }
                }
            }
        })
    }

    private fun performSearch(query: String, callback: (Boolean) -> Unit) {
        val retrofit = Retrofit.Builder()
            .baseUrl(getString(R.string.iTunesLink))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(ApiService::class.java)
        val call = api.search(query)
        call.enqueue(object : Callback<SearchResponse> {
            override fun onResponse(
                call: Call<SearchResponse>,
                response: Response<SearchResponse>
            ) {
                Log.d("SearchActivity", "Response received for query: $query")
                Log.d("SearchActivity", "Response Code: ${response.code()}")
                errorConnectionPlaceHolder.visibility = View.GONE

                if (response.isSuccessful) {
                    val trackResponse = response.body()
                    val tracks = trackResponse?.results ?: emptyList()
                    hiddenText.visibility = View.GONE
                    refreshHistoryButton.visibility = View.GONE
                    if (tracks.isNotEmpty()) {
                        trackAdapter.updateData(tracks)
                        recyclerView.adapter = trackAdapter
                        recyclerView.visibility = View.VISIBLE
                        errorSearchNothing.visibility = View.GONE
                        callback(true)
                    } else {
                        recyclerView.visibility = View.GONE
                        errorSearchNothing.visibility = View.VISIBLE
                        callback(false)
                    }
                } else {
                    recyclerView.visibility = View.GONE
                    errorSearchNothing.visibility = View.VISIBLE
                }
            }

            override fun onFailure(call: Call<SearchResponse>, t: Throwable) {
                errorConnectionPlaceHolder.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
                errorSearchNothing.visibility = View.GONE
                lastQuery = searchQuery.trim()
            }
        })


    }
    companion object {
        const val TRACK_DATA = "TRACK_DATA"
        const val SEARCH_HSITORY = "search_history"
        const val SEARCH_KEY_ON_STATE = "search_query"

    }
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_KEY_ON_STATE, searchQuery)
    }

}