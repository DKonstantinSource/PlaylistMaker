package com.example.playlistmaker.presentation.fragments.search

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.Constants.CLICK_DEBOUNCE_DELAY
import com.example.playlistmaker.Constants.IS_FAVORITE
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentSearchBinding
import com.example.playlistmaker.presentation.fragments.media_library.player.FragmentPlayer
import com.example.playlistmaker.presentation.view_model.search.SearchViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.stateViewModel


class SearchFragment : Fragment() {

    @Suppress("DEPRECATION")
    private val viewModel: SearchViewModel by stateViewModel()

    private var _binding: FragmentSearchBinding? = null
    private var job: Job? = null
    private val binding get() = _binding!!
    private lateinit var trackAdapter: TrackAdapter
    private var isClickable = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        setupRecyclerView()
        setupClickListeners()
        setupSearchListener()
        observeViewModel()
        clearHistory()
        resetButton()


        return binding.root
    }


    private fun resetButton() {
        binding.resetButton.setOnClickListener {
            clearTextField()
        }
    }


    private fun setupRecyclerView() {
        trackAdapter = TrackAdapter { track ->
            viewModel.trackClicked(track)
            getStateActivity()
        }

        binding.recycleView.adapter = trackAdapter

    }

    private fun clearHistory() {
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

                    val bundle = Bundle().apply {
                        putSerializable(FragmentPlayer.TRACK_DATA, it)
                        putBoolean(IS_FAVORITE, it.isFavorite)
                    }

                    findNavController().navigate(
                        R.id.action_searchFragment_to_fragmentPlayer,
                        bundle
                    )

                    lifecycleScope.launch {
                        delay(CLICK_DEBOUNCE_DELAY)
                        isClickable = true
                    }
                }
            }
        }

        viewModel.tracks.observe(viewLifecycleOwner) { tracks ->
            Log.d("SearchFragment", "Updated tracks: $tracks")
            tracks?.let {
                trackAdapter.updateData(it)
                getStateActivity()
            }
        }
    }


    override fun onStop() {
        super.onStop()
        viewModel.clearDateTrack()
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

        binding.resetButton.setOnClickListener {
            clearTextField()
        }

    }

    private fun setupSearchListener() {
        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.progressBar.visibility = View.VISIBLE
                if (s.isNullOrBlank()) {
                    binding.progressBar.visibility = View.GONE
                }
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


    private fun clearTextField() {
        binding.resetButton.setOnClickListener {
            binding.resetButton.visibility = View.GONE
            binding.searchEditText.text.clear()
            binding.progressBar.visibility = View.GONE
        }
    }


    private fun getStateActivity() {
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
    override fun onPause() {
        super.onPause()
        viewModel.updateTracks()
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}