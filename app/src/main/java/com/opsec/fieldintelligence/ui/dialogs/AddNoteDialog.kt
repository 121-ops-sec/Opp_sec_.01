package com.opsec.fieldintelligence.ui.dialogs

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.opsec.fieldintelligence.R
import com.opsec.fieldintelligence.databinding.DialogAddNoteBinding
import com.opsec.fieldintelligence.ui.map.MapViewModel

class AddNoteDialog : DialogFragment() {

    private val viewModel: MapViewModel by activityViewModels()

    companion object {
        const val TAG = "AddNoteDialog"
        private const val ARG_PIN_ID = "pin_id"

        fun newInstance(pinId: Long) = AddNoteDialog().apply {
            arguments = Bundle().apply { putLong(ARG_PIN_ID, pinId) }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val pinId = arguments?.getLong(ARG_PIN_ID) ?: -1L
        val binding = DialogAddNoteBinding.inflate(layoutInflater)

        return MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.add_note)
            .setView(binding.root)
            .setPositiveButton(R.string.confirm) { _, _ ->
                val title = binding.etNoteTitle.text?.toString()?.trim() ?: return@setPositiveButton
                val body = binding.etNoteBody.text?.toString()?.trim() ?: ""
                if (pinId > 0) {
                    viewModel.addNote(pinId, title, body)
                }
            }
            .setNegativeButton(R.string.cancel, null)
            .create()
    }
}
