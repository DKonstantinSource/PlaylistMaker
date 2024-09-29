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
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
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


    @SuppressLint("ResourceType", "MissingInflatedId")
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




        searchEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(searchEditText, InputMethodManager.SHOW_IMPLICIT)
            }
        }
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                resetButton.visibility = if (s.isNullOrEmpty()) {
                    View.GONE
                } else {
                    View.VISIBLE
                }
                if (s.isNullOrEmpty()) {
                    recyclerView.visibility = View.GONE
                    errorSearchNothing.visibility = View.GONE
                } else {
                    errorSearchNothing.visibility = View.GONE
                }
                searchQuery = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        resetButton.setOnClickListener {
            searchEditText.text.clear()
            searchQuery = ""
            searchEditText.clearFocus()
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(searchEditText.windowToken, 0)
            resetButton.visibility = View.GONE
        }


        refreshButton.setOnClickListener{
            lastQuery?.let { query ->
                performSearch(query) { trackFound ->
                    if (trackFound) {
                        errorConnectionPlaceHolder.visibility = View.GONE
                    } else {
                        errorConnectionPlaceHolder.visibility = View.VISIBLE
                    }
                }
            }
        }


        val backOnMainActivity = findViewById<ImageView>(R.id.backButton)
        backOnMainActivity.setOnClickListener { finish() }

        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                Log.d("SearchActivity", "Search query: $searchQuery")
                performSearch(searchQuery) { trackFound ->
                    if (trackFound) {
                        errorSearchNothing.visibility = View.GONE
                    } else {
                        errorSearchNothing.visibility = View.VISIBLE
                    }
                }
                true
            } else {
                false
            }
        }

        recyclerView = findViewById(R.id.tracks)
        recyclerView.layoutManager = LinearLayoutManager(this)
        trackAdapter = TrackAdapter()
        recyclerView.adapter = trackAdapter


        if (savedInstanceState != null) {
            searchQuery = savedInstanceState.getString(searchKeyOnState, "")
            searchEditText.setText(searchQuery)
        }
    }





    private fun performSearch(query: String, callback: (Boolean) -> Unit) {
        val retrofit = Retrofit.Builder()
            .baseUrl(getString(R.string.iTunesLink))
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(ApiService::class.java)

        val call = api.search(query)
        call.enqueue(object : Callback<SearchResponse> {
            override fun onResponse(call: Call<SearchResponse>, response: Response<SearchResponse>) {
                Log.d("SearchActivity", "Response received for query: $query")
                Log.d("SearchActivity", "Response Code: ${response.code()}")
                errorConnectionPlaceHolder.visibility = View.GONE

                if (response.isSuccessful) {
                    val trackResponse = response.body()
                    Log.d("SearchActivity", "Response Body: ${trackResponse.toString()}")

                    val tracks = trackResponse?.results ?: emptyList()
                    Log.d("SearchActivity", "Tracks found: ${tracks.size}")

                    if (tracks.isNotEmpty()) {
                        trackAdapter.updateData(tracks)
                        recyclerView.visibility = View.VISIBLE
                        errorSearchNothing.visibility = View.GONE
                        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.hideSoftInputFromWindow(searchEditText.windowToken, 0)
                        callback(true)
                    } else {
                        recyclerView.visibility = View.GONE
                        errorSearchNothing.visibility = View.VISIBLE
                        callback(false)
                    }

                } else {
                    Log.e("SearchActivity", "Response Error: ${response.code()} - ${response.errorBody()?.string()}")
                    recyclerView.visibility = View.GONE
                    errorSearchNothing.visibility = View.VISIBLE
                }
            }

            override fun onFailure(call: Call<SearchResponse>, t: Throwable) {
                Log.e("SearchActivity", "Network Error: ${t.message}")
                val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(searchEditText.windowToken, 0)
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