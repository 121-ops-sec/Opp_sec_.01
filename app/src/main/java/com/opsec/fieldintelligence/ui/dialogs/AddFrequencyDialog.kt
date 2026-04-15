package com.opsec.fieldintelligence.ui.dialogs

import android.app.Dialog
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.slider.Slider
import com.opsec.fieldintelligence.R
import com.opsec.fieldintelligence.data.model.SignalQuality
import com.opsec.fieldintelligence.databinding.DialogAddFrequencyBinding
import com.opsec.fieldintelligence.ui.map.MapViewModel

class AddFrequencyDialog : DialogFragment() {

    private val viewModel: MapViewModel by activityViewModels()

    companion object {
        const val TAG = "AddFrequencyDialog"
        private const val ARG_PIN_ID = "pin_id"

        fun newInstance(pinId: Long) = AddFrequencyDialog().apply {
            arguments = Bundle().apply { putLong(ARG_PIN_ID, pinId) }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val pinId = arguments?.getLong(ARG_PIN_ID) ?: -1L
        val binding = DialogAddFrequencyBinding.inflate(layoutInflater)

        // Set up modulation dropdown
        val modulations = resources.getStringArray(R.array.modulation_types)
        val modAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, modulations)
        binding.spinnerModulation.setAdapter(modAdapter)
        binding.spinnerModulation.setText(modulations[0], false)

        // Signal quality slider label
        val qualities = SignalQuality.values()
        binding.tvSignalQualityLabel.text = qualities[2].label // default FAIR
        binding.sliderSignalQuality.addOnChangeListener { _, value, _ ->
            binding.tvSignalQualityLabel.text = qualities[value.toInt()].label
        }

        return MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.log_frequency)
            .setView(binding.root)
            .setPositiveButton(R.string.confirm) { _, _ ->
                val mhzText = binding.etFrequencyMhz.text?.toString()?.trim() ?: return@setPositiveButton
                val mhz = mhzText.toDoubleOrNull() ?: return@setPositiveButton
                val modulation = binding.spinnerModulation.text.toString()
                val quality = qualities[binding.sliderSignalQuality.value.toInt()]
                val rxNotes = binding.etRxNotes.text?.toString()?.trim() ?: ""
                if (pinId > 0) {
                    viewModel.logFrequency(pinId, mhz, modulation, quality, rxNotes)
                }
            }
            .setNegativeButton(R.string.cancel, null)
            .create()
    }
}
