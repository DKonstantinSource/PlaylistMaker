package com.example.playlistmaker

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
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

        val sharedPreferences = getSharedPreferences("search_history", MODE_PRIVATE)
        searchHistory = SearchHistory(sharedPreferences)

        val backOnMainActivity = findViewById<ImageView>(R.id.backButton)
        backOnMainActivity.setOnClickListener { finish() }




        resetButton.setOnClickListener {
            searchEditText.text.clear()
            searchQuery = ""
            searchEditText.clearFocus()
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(searchEditText.windowToken, 0)
            resetButton.visibility = View.GONE
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


        searchEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(searchEditText, InputMethodManager.SHOW_IMPLICIT)
            }
        }

        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                refreshButton.visibility = View.VISIBLE
                Log.d("SearchActivity", "Search query: $searchQuery")
                performSearch(searchQuery) { trackFound ->
                    if (trackFound) {
                        errorSearchNothing.visibility = View.GONE
                        refreshButton.visibility = View.VISIBLE


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


        val historyTracks = searchHistory.getSearchHistory()



        trackAdapter = TrackAdapter { track ->
            searchHistory.addToHistory(track)
        }

        recyclerView.adapter = trackAdapter


        fun updateHistory() {
            if (historyTracks.isEmpty()) {
                recyclerView.visibility = View.GONE
                errorSearchNothing.visibility = View.GONE
                hiddenText.visibility = View.GONE
                refreshHistoryButton.visibility = View.GONE
            } else {
                trackAdapter.updateData(historyTracks)
                hiddenText.visibility = View.VISIBLE
                recyclerView.visibility = View.VISIBLE
                errorSearchNothing.visibility = View.GONE
                refreshHistoryButton.visibility = View.VISIBLE

            }
        }
        updateHistory()
        refreshHistoryButton.setOnClickListener{
            hiddenText.visibility = View.GONE
            recyclerView.visibility = View.GONE
            refreshHistoryButton.visibility = View.GONE
            searchHistory.clearHistory()

        }
        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                resetButton.visibility = View.VISIBLE
                Log.d("SearchActivity", "Search query: $searchQuery")
                performSearch(searchQuery) { trackFound ->
                    if (trackFound) {
                        errorSearchNothing.visibility = View.GONE
                        hiddenText.visibility = View.GONE
                        refreshHistoryButton.visibility = View.GONE
                    } else {
                        errorSearchNothing.visibility = View.VISIBLE
                    }
                }
                true


            } else {
                false
            }
        }

        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, p3: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchQuery = s.toString()
                resetButton.visibility = View.VISIBLE
                hiddenText.visibility = View.GONE
                refreshHistoryButton.visibility = View.GONE
                if (searchQuery.isNotEmpty()) {
                    performSearch(searchQuery) { trackFound ->
                        if (trackFound) {
                            errorSearchNothing.visibility = View.GONE
                        } else {
                            errorSearchNothing.visibility = View.VISIBLE
                        }
                    }
                } else {
                    trackAdapter.updateData(emptyList())
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
                    Log.d("SearchActivity", "Response Body: ${trackResponse.toString()}")

                    val tracks = trackResponse?.results ?: emptyList()
                    Log.d("SearchActivity", "Tracks found: ${tracks.size}")
                    hiddenText.visibility = View.GONE
                    refreshHistoryButton.visibility = View.GONE
                    if (tracks.isNotEmpty()) {
                        trackAdapter.updateData(tracks)
                        recyclerView.visibility = View.VISIBLE
                        errorSearchNothing.visibility = View.GONE
                        callback(true)
                    } else {
                        recyclerView.visibility = View.GONE
                        errorSearchNothing.visibility = View.VISIBLE
                        callback(false)
                    }
                } else {
                    Log.e(
                        "SearchActivity",
                        "Response Error: ${response.code()} - ${response.errorBody()?.string()}"
                    )
                    recyclerView.visibility = View.GONE
                    errorSearchNothing.visibility = View.VISIBLE
                }
            }

            override fun onFailure(call: Call<SearchResponse>, t: Throwable) {
                Log.e("SearchActivity", "Network Error: ${t.message}")
                errorConnectionPlaceHolder.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
                errorSearchNothing.visibility = View.GONE
                lastQuery = searchQuery
            }
        })

    }
    companion object {
        const val searchKeyOnState = "search_query"

    }
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(searchKeyOnState, searchQuery)
    }

}