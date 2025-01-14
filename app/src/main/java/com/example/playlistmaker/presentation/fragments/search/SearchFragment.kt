package com.example.playlistmaker.presentation.fragments.search

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import com.example.playlistmaker.Constants.CLICK_DEBOUNCE_DELAY
import com.example.playlistmaker.databinding.FragmentSearchBinding
import com.example.playlistmaker.presentation.ui.player.PlayerActivity
import com.example.playlistmaker.presentation.ui.player.PlayerActivity.Companion.TRACK_DATA
import com.example.playlistmaker.presentation.view_model.search.SearchViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {

    private val viewModel: SearchViewModel by viewModel()
    private lateinit var binding: FragmentSearchBinding
    private lateinit var trackAdapter: TrackAdapter
    private var isClickable = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)

        setupRecyclerView()
        setupClickListeners()
        setupSearchListener()
        observeViewModel()
        clearHistory()
        clearTextField()
        return binding.root


    }

    private fun setupRecyclerView() {
        trackAdapter = TrackAdapter { track ->
            viewModel.trackClicked(track)
            getStateActivity()
        }

        binding.recycleView.adapter = trackAdapter

    }

    fun clearHistory() {
        binding.clearHistoryButton.setOnClickListener {
            binding.clearHistoryButton.visibility = View.GONE
            binding.prevSearch.visibility = View.GONE
            viewModel.clearHistory()


        }
    }

    private fun observeViewModel() {
        viewModel.selectedTrack.observe(viewLifecycleOwner) { track ->
            track?.let {
                if (isClickable) {
                    isClickable = false
                    val intent = Intent(requireContext(), PlayerActivity::class.java).apply {
                        putExtra(TRACK_DATA, it)
                    }
                    startActivity(intent)
                    Handler(Looper.getMainLooper()).postDelayed(
                        { isClickable = true },
                        CLICK_DEBOUNCE_DELAY
                    )
                }
            }
        }

        viewModel.tracks.observe(viewLifecycleOwner) { tracks ->
            tracks?.let {
                trackAdapter.updateData(it)
                getStateActivity()
            }
        }
    }

    private fun setupClickListeners() {
        binding.searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val query = binding.searchEditText.text.toString().trim()
                viewModel.onSearchQueryChanged(query)
                binding.progressBar.visibility = View.VISIBLE
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

    fun clearTextField() {
        binding.resetButton.setOnClickListener {
            binding.resetButton.visibility = View.GONE
            binding.searchEditText.text.clear()
            binding.progressBar.visibility = View.GONE
        }
    }


    fun getStateActivity() {
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

    companion object {
        fun newInstance() = SearchFragment()
    }
}