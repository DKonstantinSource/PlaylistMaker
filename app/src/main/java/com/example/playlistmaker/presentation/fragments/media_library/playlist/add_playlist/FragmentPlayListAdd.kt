package com.example.playlistmaker.presentation.fragments.media_library.playlist.add_playlist

import android.app.AlertDialog
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.playlistmaker.databinding.FragmentPlaylistAddBinding
import com.example.playlistmaker.domain.model.Playlist
import com.example.playlistmaker.PhotoPickerUtil
import com.example.playlistmaker.R
import com.example.playlistmaker.presentation.ui.host.HostActivity
import com.example.playlistmaker.presentation.view_model.library.LibraryViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import org.koin.androidx.viewmodel.ext.android.viewModel

class FragmentPlayListAdd : Fragment() {

    private var _binding: FragmentPlaylistAddBinding? = null
    private val binding get() = _binding!!

    private val libraryViewModel: LibraryViewModel by viewModel()
    private var playlistId: Long = 0L
    private var coverImagePath: String? = null
    private var currentPlaylist: Playlist? = null

    private val activeColor by lazy { requireContext().getColor(R.color.border_on_focus_button) }
    private val defaultColor by lazy { requireContext().getColor(R.color.border_edit_text_button) }

    private val selectImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                coverImagePath = PhotoPickerUtil.copyImageToAppStorage(requireContext(), uri)
                Glide.with(this)
                    .load(coverImagePath)
                    .placeholder(R.drawable.image_placeholder)
                    .into(binding.imageNewPlaylist)

                binding.imageNewPlaylist.setImageURI(uri)
                binding.buttonImageAdd.visibility = View.GONE
                updateUIState()
                binding.imageNewPlaylist.background = null
                binding.imageNewPlaylist.shapeAppearanceModel =
                    binding.imageNewPlaylist.shapeAppearanceModel
                        .toBuilder()
                        .setAllCorners(
                            com.google.android.material.shape.CornerFamily.ROUNDED,
                            8 * resources.displayMetrics.density
                        )
                        .build()
            }
        }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistAddBinding.inflate(inflater, container, false)
        (activity as? HostActivity)?.setBottomNavigationVisibility(false)
        libraryViewModel.lastCreatedPlaylistName.observe(viewLifecycleOwner) { message ->
            showSnackBar(message)
        }

        return binding.root
    }

    private fun showSnackBar(message: String?) {
        message?.let {
            val snackbar = Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG)
            val textView =
                snackbar.view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
            val typeface = ResourcesCompat.getFont(requireContext(), R.font.ys_display_regular)
            textView.setTextSize(14f)
            textView.setTypeface(typeface)
            snackbar.show()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        arguments?.let {
            playlistId = it.getLong("playlistId")
            if (playlistId != 0L) {
                binding.saveNewPlayList.text = getString(R.string.save)
                binding.titleAddPlaylist.text = getString(R.string.edit)
                binding.buttonImageAdd.visibility = View.GONE
                libraryViewModel.loadPlaylist(playlistId)
                libraryViewModel.currentPlaylist.observe(viewLifecycleOwner) { playlist ->
                    playlist?.let {
                        currentPlaylist = it
                        fillPlaylistFields(it)
                    }
                }


                requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
                    handleBackPress()
                }

            }
        }


        binding.saveNewPlayList.setOnClickListener {
            val name = binding.titleName.text.toString().trim()
            val description = binding.playlistDescription.text.toString().trim()

            if (playlistId != 0L && currentPlaylist != null) {
                val updatedImagePath = coverImagePath ?: currentPlaylist!!.imagePath

                val updatedPlaylist = currentPlaylist!!.copy(
                    name = name,
                    description = description,
                    imagePath = updatedImagePath
                )
                libraryViewModel.updatePlaylist(updatedPlaylist)
            } else {
                savePlaylist()
            }

            findNavController().navigateUp()
        }




        libraryViewModel.loadPlaylist(playlistId)
        binding.titleName.addTextChangedListener { updateUIState() }
        binding.playlistDescription.addTextChangedListener { updateUIState() }

        binding.imageNewPlaylist.setOnClickListener {
            selectImageLauncher.launch("image/*")
        }


        binding.backButton.setOnClickListener {
            handleBackPress()
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            handleBackPress()
        }
    }


    private fun fillPlaylistFields(playlist: Playlist) {
        binding.titleName.setText(playlist.name)
        binding.playlistDescription.setText(playlist.description ?: "")
        coverImagePath = playlist.imagePath

        Glide.with(this)
            .load(coverImagePath)
            .placeholder(R.drawable.image_placeholder)
            .into(binding.imageNewPlaylist)
    }


    private fun updateUIState() {
        val titleNotEmpty = !binding.titleName.text.isNullOrBlank()
        binding.saveNewPlayList.isEnabled = titleNotEmpty

        val descriptionNotEmpty = !binding.playlistDescription.text.isNullOrBlank()

        binding.saveNewPlayList.backgroundTintList =
            ColorStateList.valueOf(if (titleNotEmpty) activeColor else defaultColor)

        binding.titleName.backgroundTintList =
            ColorStateList.valueOf(if (titleNotEmpty) activeColor else defaultColor)

        binding.playlistDescription.backgroundTintList =
            ColorStateList.valueOf(if (descriptionNotEmpty) activeColor else defaultColor)
    }


    private fun savePlaylist(existingPlaylist: Playlist? = null) {
        val name = binding.titleName.text?.toString()?.trim()
        val description = binding.playlistDescription.text?.toString()?.trim() ?: ""

        if (name.isNullOrEmpty()) {
            Toast.makeText(context, "Введите название плейлиста", Toast.LENGTH_SHORT).show()
            return
        }

        val defaultImagePath =
            "android.resource://${requireContext().packageName}/${R.drawable.image_placeholder}"

        val playlist = existingPlaylist?.copy(
            name = name,
            description = description,
            imagePath = coverImagePath ?: existingPlaylist.imagePath
        ) ?: Playlist(
            id = 0,
            name = name,
            description = description,
            imagePath = coverImagePath ?: defaultImagePath,
            tracks = emptyList(),
            trackCount = 0
        )

        if (existingPlaylist != null) {
            libraryViewModel.updatePlaylist(playlist)
            showSnackBar("Плейлист \"$name\" обновлен")
        } else {
            libraryViewModel.createPlaylist(playlist)
            showSnackBar("Плейлист \"$name\" создан")
        }

        findNavController().navigateUp()
    }

    private fun handleBackPress() {
        if (isDataModified()) {
            confirmDialog()
        } else {
            findNavController().navigateUp()
        }
    }

    private fun isDataModified(): Boolean {
        return currentPlaylist?.let { original ->
            val titleChanged = binding.titleName.text.toString().trim() != original.name
            val descriptionChanged =
                binding.playlistDescription.text.toString().trim() != (original.description ?: "")
            val coverChanged = coverImagePath != original.imagePath

            titleChanged || descriptionChanged || coverChanged
        } ?: (
                !binding.titleName.text.isNullOrEmpty() ||
                        !binding.playlistDescription.text.isNullOrEmpty() ||
                        coverImagePath != null
                )
    }


    private fun confirmDialog() {
        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.askCancelRequset)
            .setMessage(R.string.askUnsavedDate)
            .setNeutralButton(R.string.decline, null)
            .setNegativeButton(R.string.cancel) { _, _ ->
                findNavController().navigateUp()
            }
            .show()
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
            ?.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
        dialog.getButton(AlertDialog.BUTTON_NEUTRAL)
            ?.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))

    }
    override fun onResume() {
        super.onResume()
        updateUIState()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        (activity as? HostActivity)?.setBottomNavigationVisibility(true)
    }
}
