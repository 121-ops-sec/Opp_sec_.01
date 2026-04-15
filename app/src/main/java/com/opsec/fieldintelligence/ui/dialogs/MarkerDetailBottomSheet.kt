package com.opsec.fieldintelligence.ui.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.opsec.fieldintelligence.databinding.BottomSheetMarkerDetailBinding
import com.opsec.fieldintelligence.ui.frequency.FrequencyAdapter
import com.opsec.fieldintelligence.ui.hazard.HazardAdapter
import com.opsec.fieldintelligence.ui.map.MapViewModel
import com.opsec.fieldintelligence.ui.notes.NoteAdapter
import java.util.Locale

class MarkerDetailBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomSheetMarkerDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MapViewModel by activityViewModels()

    companion object {
        const val TAG = "MarkerDetailBottomSheet"
        private const val ARG_PIN_ID = "pin_id"

        fun newInstance(pinId: Long) = MarkerDetailBottomSheet().apply {
            arguments = Bundle().apply { putLong(ARG_PIN_ID, pinId) }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetMarkerDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val pinId = arguments?.getLong(ARG_PIN_ID) ?: return

        // Populate pin header from allPins observable
        viewModel.allPins.observe(viewLifecycleOwner) { pins ->
            val pin = pins.firstOrNull { it.id == pinId } ?: return@observe
            binding.tvPinTitle.text = pin.title
            binding.tvPinType.text = pin.markerType.name
            binding.tvPinCoords.text =
                String.format(Locale.US, "%.5f, %.5f", pin.latitude, pin.longitude)
        }

        // Notes
        val noteAdapter = NoteAdapter(onNoteClick = {})
        binding.recyclerDetailNotes.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = noteAdapter
        }
        viewModel.notesForPin(pinId).observe(viewLifecycleOwner) { notes ->
            noteAdapter.submitList(notes)
            binding.tvNoNotes.visibility = if (notes.isEmpty()) View.VISIBLE else View.GONE
        }

        // Frequencies
        val freqAdapter = FrequencyAdapter()
        binding.recyclerDetailFreqs.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = freqAdapter
        }
        viewModel.freqsForPin(pinId).observe(viewLifecycleOwner) { freqs ->
            freqAdapter.submitList(freqs)
            binding.tvNoFreqs.visibility = if (freqs.isEmpty()) View.VISIBLE else View.GONE
        }

        // Hazards
        val hazardAdapter = HazardAdapter()
        binding.recyclerDetailHazards.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = hazardAdapter
        }
        viewModel.hazardsForPin(pinId).observe(viewLifecycleOwner) { hazards ->
            hazardAdapter.submitList(hazards)
            binding.tvNoHazards.visibility = if (hazards.isEmpty()) View.VISIBLE else View.GONE
        }

        // Action buttons
        binding.btnDeletePin.setOnClickListener {
            val pin = viewModel.allPins.value?.firstOrNull { it.id == pinId } ?: return@setOnClickListener
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Delete pin?")
                .setMessage("This will also delete all notes, frequencies, and hazards attached to \"${pin.title}\".")
                .setPositiveButton("Delete") { _, _ ->
                    viewModel.deletePin(pin)
                    dismiss()
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        binding.btnAddNoteToPin.setOnClickListener {
            AddNoteDialog.newInstance(pinId)
                .show(parentFragmentManager, AddNoteDialog.TAG)
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
