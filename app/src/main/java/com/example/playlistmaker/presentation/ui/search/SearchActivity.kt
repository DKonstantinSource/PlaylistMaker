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
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.Constants.CLICK_DEBOUNCE_DELAY
import com.example.playlistmaker.Creator.Creator
import com.example.playlistmaker.databinding.ActivitySearchBinding
import com.example.playlistmaker.presentation.ui.player.PlayerActivity
import com.example.playlistmaker.presentation.view_model.search.SearchViewModel
import com.example.playlistmaker.presentation.view_model.search.SearchViewModelFactory


@SuppressLint("ResourceType", "MissingInflatedId", "CutPasteId")

class SearchActivity : AppCompatActivity() {
    private lateinit var viewModel: SearchViewModel
    private lateinit var trackAdapter: TrackAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var binding: ActivitySearchBinding
    private var isClickable = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val handler = Handler(
            Looper.getMainLooper()
        )
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val manageSearchHistory = Creator.createManageSearchHistoryUseCase(sharedPreferences)
        val searchTracksInteractorImpl = Creator.createSearchTracksUseCase()

        viewModel = ViewModelProvider(
            this,
            SearchViewModelFactory(
                manageSearchHistory,
                searchTracksInteractorImpl
            )
        ).get(SearchViewModel::class.java)
        recyclerView = binding.recycleView

        trackAdapter = TrackAdapter { track ->
            viewModel.trackClicked(track)
            getStateActivity()
        }

        viewModel.selectedTrack.observe(this) { track ->
            track?.let {
                if (isClickable) {
                    isClickable = false
                    val intent = Intent(this, PlayerActivity::class.java).apply {
                        putExtra(TRACK_DATA, it)
                    }
                    startActivity(intent)
                    handler.postDelayed({ isClickable = true }, CLICK_DEBOUNCE_DELAY)
                }
            }
        }


        viewModel.tracks.observe(this, Observer { tracks ->
            tracks?.let {

                trackAdapter.updateData(it)
                recyclerView.adapter = trackAdapter
                getStateActivity()
            }

        })
        setupClickListeners()
        setupSearchListener()
        backOnScreen()
        clearHistory()
        clearTextField()
        getStateActivity()

    }

    private fun setupClickListeners() {
        binding.searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val query = binding.searchEditText.text.toString().trim()
                viewModel.onSearchQueryChanged(query)

                true
            } else {
                false
            }
        }
    }

    private fun setupSearchListener() {
        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.progressBar.visibility = View.VISIBLE
                if (s.toString().isEmpty()) {
                    binding.resetButton.visibility = View.GONE
                } else {
                    binding.resetButton.visibility = View.VISIBLE
                }
                binding.errorSearchNothing.visibility = View.GONE
                binding.prevSearch.visibility = View.GONE
                binding.recycleView.visibility = View.GONE
                binding.clearHistoryButton.visibility = View.GONE
                viewModel.onSearchQueryChanged(s.toString())
            }
        })
    }

    fun clearHistory() {
        binding.clearHistoryButton.setOnClickListener {
            binding.clearHistoryButton.visibility = View.GONE
            binding.prevSearch.visibility = View.GONE
            viewModel.clearHistory()
            getStateActivity()

        }
    }

    fun getStateActivity() {
        binding.progressBar.visibility = View.GONE
        if (viewModel.tracks.value!!.isEmpty() and viewModel.getQuery()
                .isNotEmpty() and !viewModel.getStoryState() and viewModel.getStateAfterSearch()
        ) {
            binding.errorSearchNothing.visibility = View.VISIBLE
            binding.clearHistoryButton.visibility = View.GONE
            binding.prevSearch.visibility = View.GONE
            binding.progressBar.visibility = View.GONE
            binding.recycleView.visibility = View.VISIBLE
            Log.e("Ardad", "1")
        } else if (viewModel.tracks.value!!.isNotEmpty() and viewModel.getQuery()
                .isEmpty() and !viewModel.getStoryState() and viewModel.getStateAfterSearch()
        ) {
            binding.errorSearchNothing.visibility = View.GONE
            binding.clearHistoryButton.visibility = View.VISIBLE
            binding.prevSearch.visibility = View.VISIBLE
            binding.progressBar.visibility = View.GONE
            binding.recycleView.visibility = View.VISIBLE
            Log.e("Ardad", "2")
        } else if (viewModel.tracks.value!!.isNotEmpty() and viewModel.getQuery()
                .isNotEmpty() and !viewModel.getStoryState() and viewModel.getStateAfterSearch()
        ) {
            binding.clearHistoryButton.visibility = View.GONE
            binding.prevSearch.visibility = View.GONE
            binding.errorSearchNothing.visibility = View.GONE
            binding.progressBar.visibility = View.GONE
            binding.recycleView.visibility = View.VISIBLE
            Log.e("Ardad", "3")
        } else if (viewModel.tracks.value!!.isNotEmpty() and viewModel.getQuery()
                .isNotEmpty() and !viewModel.getStoryState() and !viewModel.getStateAfterSearch()
        ) {
            binding.clearHistoryButton.visibility = View.GONE
            binding.prevSearch.visibility = View.GONE
            binding.progressBar.visibility = View.GONE
            binding.recycleView.visibility = View.VISIBLE
            Log.e("Ardad", "4")
        } else if (viewModel.tracks.value!!.isEmpty() and viewModel.getQuery()
                .isEmpty() and viewModel.getStoryState() and !viewModel.getStateAfterSearch()
        ) {
            binding.clearHistoryButton.visibility = View.GONE
            binding.prevSearch.visibility = View.GONE
            binding.progressBar.visibility = View.GONE
            binding.recycleView.visibility = View.VISIBLE
        } else if (viewModel.tracks.value!!.isEmpty() and viewModel.getQuery()
                .isEmpty() and !viewModel.getStoryState() and !viewModel.getStateAfterSearch()
        ) {
            binding.clearHistoryButton.visibility = View.GONE
            binding.progressBar.visibility = View.GONE
            binding.recycleView.visibility = View.VISIBLE
            Log.e("Ardad", "5")
        } else if (viewModel.tracks.value!!.isNotEmpty() and viewModel.getQuery()
                .isNotEmpty() and viewModel.getStoryState() and viewModel.getStateAfterSearch()
        ) {
            binding.clearHistoryButton.visibility = View.GONE
            binding.progressBar.visibility = View.GONE
            binding.recycleView.visibility = View.VISIBLE
            Log.e("Ardad", "6")
        } else if (viewModel.tracks.value!!.isEmpty() and viewModel.getQuery()
                .isNotEmpty() and viewModel.getStoryState() and !viewModel.getStateAfterSearch()
        ) {
            binding.clearHistoryButton.visibility = View.GONE
            binding.errorSearchNothing.visibility = View.VISIBLE
            binding.progressBar.visibility = View.GONE
            binding.recycleView.visibility = View.VISIBLE
            Log.e("Ardad", "7")
        } else if (viewModel.tracks.value!!.isEmpty() and viewModel.getQuery()
                .isNotEmpty() and !viewModel.getStoryState() and !viewModel.getStateAfterSearch()
        ) {
            binding.clearHistoryButton.visibility = View.GONE
            binding.errorSearchNothing.visibility = View.VISIBLE
            binding.progressBar.visibility = View.GONE
            binding.recycleView.visibility = View.VISIBLE
            Log.e("Ardad", "8")
        } else if (viewModel.tracks.value!!.isNotEmpty() and viewModel.getQuery()
                .isEmpty() and viewModel.getStoryState() and !viewModel.getStateAfterSearch()
        ) {
            binding.clearHistoryButton.visibility = View.VISIBLE
            binding.errorSearchNothing.visibility = View.GONE
            binding.prevSearch.visibility = View.VISIBLE
            binding.progressBar.visibility = View.GONE
            binding.recycleView.visibility = View.VISIBLE
            Log.e("Ardad", "9")
        } else if (viewModel.tracks.value!!.isNotEmpty() and viewModel.getQuery()
                .isNotEmpty() and viewModel.getStoryState() and !viewModel.getStateAfterSearch()
        ) {
            binding.clearHistoryButton.visibility = View.GONE
            binding.errorSearchNothing.visibility = View.GONE
            binding.prevSearch.visibility = View.GONE
            binding.progressBar.visibility = View.GONE
            binding.recycleView.visibility = View.VISIBLE
            Log.e("Ardad", "10")
        } else if (viewModel.tracks.value!!.isNotEmpty() and viewModel.getQuery()
                .isEmpty() and viewModel.getStoryState() and viewModel.getStateAfterSearch()
        ) {
            binding.clearHistoryButton.visibility = View.VISIBLE
            binding.errorSearchNothing.visibility = View.GONE
            binding.prevSearch.visibility = View.VISIBLE
            binding.progressBar.visibility = View.GONE
            binding.recycleView.visibility = View.VISIBLE
            Log.e("Ardad", "11")
        } else {
            binding.errorSearchNothing.visibility = View.GONE
            binding.progressBar.visibility = View.GONE
            Log.e("Ardad", "12")
        }

    }

    fun clearTextField() {
        binding.resetButton.setOnClickListener {
            binding.resetButton.visibility = View.GONE
            binding.searchEditText.text.clear()
            getStateActivity()
        }
    }


    private fun backOnScreen() {
        binding.backButton.setOnClickListener {
            finish()
        }
    }

    companion object {
        const val TRACK_DATA = "TRACK_DATA"
    }
}