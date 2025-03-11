package com.example.playlistmaker.presentation.fragments.media_library.playlist.add_playlist

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.databinding.FragmentPlaylistAddBinding
import com.example.playlistmaker.domain.model.Playlist
import com.example.playlistmaker.PhotoPickerUtil
import com.example.playlistmaker.R
import com.example.playlistmaker.presentation.view_model.library.LibraryViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel

class FragmentPlayListAdd : Fragment() {

    private var _binding: FragmentPlaylistAddBinding? = null
    private val binding get() = _binding!!

    private val libraryViewModel: LibraryViewModel by viewModel()

    private var coverImagePath: String? = null

    private val activeColor by lazy { requireContext().getColor(R.color.border_on_focus) }
    private val defaultColor by lazy { requireContext().getColor(R.color.border_edit_text) }

    private val selectImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                coverImagePath = PhotoPickerUtil.copyImageToAppStorage(requireContext(), uri)
                binding.imageNewPlaylist.setImageURI(uri)
                binding.buttonImageAdd.visibility = View.GONE
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistAddBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val watcher = { updateUIState() }

        binding.titleName.addTextChangedListener { watcher() }
        binding.playlistDescription.addTextChangedListener { watcher() }

        binding.imageNewPlaylist.setOnClickListener {
            selectImageLauncher.launch("image/*")
        }

        binding.saveNewPlayList.setOnClickListener {
            savePlaylist()
        }

        binding.buttonBackAddPlaylist.setOnClickListener {
            handleBackPress()
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            handleBackPress()
        }
    }

    private fun updateUIState() {
        val titleNotEmpty = !binding.titleName.text.isNullOrBlank()
        val descriptionNotEmpty = !binding.playlistDescription.text.isNullOrBlank()

        val isButtonActive = titleNotEmpty && descriptionNotEmpty
        binding.saveNewPlayList.isEnabled = isButtonActive
        binding.saveNewPlayList.backgroundTintList =
            ColorStateList.valueOf(if (isButtonActive) activeColor else defaultColor)

        binding.titleName.backgroundTintList =
            ColorStateList.valueOf(if (titleNotEmpty) activeColor else defaultColor)
        binding.playlistDescription.backgroundTintList =
            ColorStateList.valueOf(if (descriptionNotEmpty) activeColor else defaultColor)
    }

    private fun savePlaylist() {
        val name = binding.titleName.text?.toString()?.trim()
        val description = binding.playlistDescription.text?.toString()?.trim()

        if (name.isNullOrEmpty()) {
            Toast.makeText(context, "Введите название плейлиста", Toast.LENGTH_SHORT).show()
            return
        }
        val defaultImagePath =
            "android.resource://${requireContext().packageName}/${R.drawable.image_placeholder}"

        val playlist = Playlist(
            id = 0,
            name = name,
            description = description ?: "",
            imagePath = coverImagePath.takeIf { !it.isNullOrEmpty() } ?: defaultImagePath,
            tracks = emptyList(),
            trackCount = 0
        )
        libraryViewModel.createPlaylist(playlist)
        Toast.makeText(context, "Плейлист \"$name\" создан", Toast.LENGTH_SHORT).show()
        findNavController().navigateUp()
    }

    private fun handleBackPress() {
        if (isDataModified()) {
            showConfirmExitDialog()
        } else {
            findNavController().navigateUp()
        }
    }

    private fun isDataModified(): Boolean {
        return !binding.titleName.text.isNullOrEmpty() || coverImagePath != null
    }

    private fun showConfirmExitDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Завершить создание плейлиста?")
            .setMessage("Все несохраненные данные будут потеряны")
            .setPositiveButton("Завершить") { _, _ ->
                findNavController().navigateUp()
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
