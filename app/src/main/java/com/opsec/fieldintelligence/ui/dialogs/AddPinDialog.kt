package com.opsec.fieldintelligence.ui.dialogs

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.opsec.fieldintelligence.R
import com.opsec.fieldintelligence.data.model.MarkerType
import com.opsec.fieldintelligence.databinding.DialogAddPinBinding
import com.opsec.fieldintelligence.ui.map.MapViewModel
import java.util.Locale

class AddPinDialog : DialogFragment() {

    private val viewModel: MapViewModel by activityViewModels()

    companion object {
        const val TAG = "AddPinDialog"
        private const val ARG_LAT = "lat"
        private const val ARG_LON = "lon"

        fun newInstance(lat: Double, lon: Double) = AddPinDialog().apply {
            arguments = Bundle().apply {
                putDouble(ARG_LAT, lat)
                putDouble(ARG_LON, lon)
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val lat = arguments?.getDouble(ARG_LAT) ?: 0.0
        val lon = arguments?.getDouble(ARG_LON) ?: 0.0

        val binding = DialogAddPinBinding.inflate(layoutInflater)
        binding.tvPinLocation.text = String.format(Locale.US, "%.5f, %.5f", lat, lon)

        return MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.add_pin)
            .setView(binding.root)
            .setPositiveButton(R.string.confirm) { _, _ ->
                val title = binding.etPinTitle.text?.toString()?.trim()
                    .takeIf { !it.isNullOrBlank() } ?: "Pin"
                val type = when {
                    binding.rbNote.isChecked -> MarkerType.NOTE
                    binding.rbFrequency.isChecked -> MarkerType.FREQUENCY
                    binding.rbHazard.isChecked -> MarkerType.HAZARD
                    binding.rbInfrastructure.isChecked -> MarkerType.INFRASTRUCTURE
                    else -> MarkerType.GENERIC
                }
                viewModel.dropPin(lat, lon, title, type)
            }
            .setNegativeButton(R.string.cancel, null)
            .create()
    }
}
