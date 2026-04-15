package com.opsec.fieldintelligence.ui.notes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.opsec.fieldintelligence.databinding.FragmentNoteListBinding
import com.opsec.fieldintelligence.ui.dialogs.AddNoteDialog
import com.opsec.fieldintelligence.ui.map.MapViewModel

class NoteListFragment : Fragment() {

    private var _binding: FragmentNoteListBinding? = null
    private val binding get() = _binding!!

    private val noteViewModel: NoteListViewModel by viewModels()
    private val mapViewModel: MapViewModel by activityViewModels()

    private val adapter = NoteAdapter(
        onNoteClick = { /* could navigate to detail or show edit dialog */ },
        onNoteLongClick = { note ->
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Delete note?")
                .setMessage("\"${note.title}\" will be permanently removed.")
                .setPositiveButton("Delete") { _, _ -> noteViewModel.deleteNote(note) }
                .setNegativeButton("Cancel", null)
                .show()
            true
        }
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNoteListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerNotes.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@NoteListFragment.adapter
        }

        noteViewModel.allNotes.observe(viewLifecycleOwner) { notes ->
            adapter.submitList(notes)
            binding.tvEmpty.visibility = if (notes.isEmpty()) View.VISIBLE else View.GONE
        }

        binding.fabAddNote.setOnClickListener {
            // Drop a pin at current location, then open note dialog
            val location = mapViewModel.currentLocation.value
            if (location != null) {
                mapViewModel.dropPin(
                    location.latitude,
                    location.longitude,
                    "Quick Note",
                    com.opsec.fieldintelligence.data.model.MarkerType.NOTE
                )
                // In a full implementation, we'd observe the newly created pin ID and pass it
                // to AddNoteDialog. For now open dialog with a pending-pin flow.
            }
            AddNoteDialog.newInstance(pinId = -1L)
                .show(childFragmentManager, AddNoteDialog.TAG)
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
