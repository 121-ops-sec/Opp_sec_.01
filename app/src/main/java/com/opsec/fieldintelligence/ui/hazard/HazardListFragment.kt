package com.opsec.fieldintelligence.ui.hazard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.opsec.fieldintelligence.databinding.FragmentHazardListBinding
import com.opsec.fieldintelligence.ui.dialogs.AddHazardDialog
import com.opsec.fieldintelligence.ui.map.MapViewModel

class HazardListFragment : Fragment() {

    private var _binding: FragmentHazardListBinding? = null
    private val binding get() = _binding!!

    private val hazardViewModel: HazardListViewModel by viewModels()
    private val mapViewModel: MapViewModel by activityViewModels()

    private val adapter = HazardAdapter(
        onLongClick = { hazard ->
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Delete hazard?")
                .setMessage("${hazard.severity.label} — ${hazard.category}")
                .setPositiveButton("Delete") { _, _ -> hazardViewModel.deleteHazard(hazard) }
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
        _binding = FragmentHazardListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerHazards.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@HazardListFragment.adapter
        }

        hazardViewModel.activeHazards.observe(viewLifecycleOwner) { hazards ->
            adapter.submitList(hazards)
            binding.tvEmpty.visibility = if (hazards.isEmpty()) View.VISIBLE else View.GONE
        }

        binding.fabAddHazard.setOnClickListener {
            AddHazardDialog.newInstance(pinId = -1L)
                .show(childFragmentManager, AddHazardDialog.TAG)
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
