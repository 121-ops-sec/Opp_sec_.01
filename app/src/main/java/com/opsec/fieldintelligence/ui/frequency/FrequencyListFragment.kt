package com.opsec.fieldintelligence.ui.frequency

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.opsec.fieldintelligence.databinding.FragmentFrequencyListBinding
import com.opsec.fieldintelligence.ui.dialogs.AddFrequencyDialog
import com.opsec.fieldintelligence.ui.map.MapViewModel

class FrequencyListFragment : Fragment() {

    private var _binding: FragmentFrequencyListBinding? = null
    private val binding get() = _binding!!

    private val freqViewModel: FrequencyListViewModel by viewModels()
    private val mapViewModel: MapViewModel by activityViewModels()

    private val adapter = FrequencyAdapter(
        onLongClick = { log ->
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Delete frequency log?")
                .setMessage(String.format("%.3f MHz — %s", log.frequencyMhz, log.modulationType))
                .setPositiveButton("Delete") { _, _ -> freqViewModel.deleteLog(log) }
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
        _binding = FragmentFrequencyListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerFrequencies.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@FrequencyListFragment.adapter
        }

        freqViewModel.allLogs.observe(viewLifecycleOwner) { logs ->
            adapter.submitList(logs)
            binding.tvEmpty.visibility = if (logs.isEmpty()) View.VISIBLE else View.GONE
        }

        binding.fabLogFrequency.setOnClickListener {
            AddFrequencyDialog.newInstance(pinId = -1L)
                .show(childFragmentManager, AddFrequencyDialog.TAG)
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
