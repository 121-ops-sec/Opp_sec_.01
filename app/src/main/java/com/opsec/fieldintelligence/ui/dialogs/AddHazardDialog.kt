package com.opsec.fieldintelligence.ui.dialogs

import android.app.Dialog
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.opsec.fieldintelligence.R
import com.opsec.fieldintelligence.data.model.HazardSeverity
import com.opsec.fieldintelligence.databinding.DialogAddHazardBinding
import com.opsec.fieldintelligence.ui.map.MapViewModel

class AddHazardDialog : DialogFragment() {

    private val viewModel: MapViewModel by activityViewModels()
    private var selectedExpiryMs: Long? = null

    companion object {
        const val TAG = "AddHazardDialog"
        private const val ARG_PIN_ID = "pin_id"

        fun newInstance(pinId: Long) = AddHazardDialog().apply {
            arguments = Bundle().apply { putLong(ARG_PIN_ID, pinId) }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val pinId = arguments?.getLong(ARG_PIN_ID) ?: -1L
        val binding = DialogAddHazardBinding.inflate(layoutInflater)

        // Severity dropdown
        val severities = HazardSeverity.values().map { it.label }
        val sevAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, severities)
        binding.spinnerSeverity.setAdapter(sevAdapter)
        binding.spinnerSeverity.setText(severities[0], false)

        // Category dropdown
        val categories = resources.getStringArray(R.array.hazard_categories)
        val catAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, categories)
        binding.spinnerCategory.setAdapter(catAdapter)
        binding.spinnerCategory.setText(categories[0], false)

        // Expiry date picker
        binding.cbSetExpiry.setOnCheckedChangeListener { _, checked ->
            if (checked) {
                val picker = MaterialDatePicker.Builder.datePicker()
                    .setTitleText(R.string.hazard_expires)
                    .build()
                picker.addOnPositiveButtonClickListener { ms ->
                    selectedExpiryMs = ms
                }
                picker.show(parentFragmentManager, "date_picker")
            } else {
                selectedExpiryMs = null
            }
        }

        return MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.add_hazard)
            .setView(binding.root)
            .setPositiveButton(R.string.confirm) { _, _ ->
                val severityLabel = binding.spinnerSeverity.text.toString()
                val severity = HazardSeverity.values()
                    .firstOrNull { it.label == severityLabel } ?: HazardSeverity.LOW
                val category = binding.spinnerCategory.text.toString()
                val description = binding.etHazardDescription.text?.toString()?.trim() ?: ""
                if (pinId > 0 && description.isNotBlank()) {
                    viewModel.addHazard(pinId, severity, category, description, selectedExpiryMs)
                }
            }
            .setNegativeButton(R.string.cancel, null)
            .create()
    }
}
