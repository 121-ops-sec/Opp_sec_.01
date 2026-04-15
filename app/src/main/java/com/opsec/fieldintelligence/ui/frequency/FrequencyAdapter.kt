package com.opsec.fieldintelligence.ui.frequency

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.opsec.fieldintelligence.data.model.FrequencyLog
import com.opsec.fieldintelligence.databinding.ItemFrequencyBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class FrequencyAdapter(
    private val onLongClick: (FrequencyLog) -> Boolean = { false }
) : ListAdapter<FrequencyLog, FrequencyAdapter.FrequencyViewHolder>(FrequencyDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FrequencyViewHolder {
        val binding = ItemFrequencyBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return FrequencyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FrequencyViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class FrequencyViewHolder(private val binding: ItemFrequencyBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val dateFormat = SimpleDateFormat("MMM d, HH:mm", Locale.getDefault())

        fun bind(log: FrequencyLog) {
            binding.tvFrequencyMhz.text = String.format(Locale.US, "%.3f MHz", log.frequencyMhz)
            binding.tvModulation.text = log.modulationType
            binding.tvSignalQuality.text = log.signalQuality.label
            binding.tvFreqDate.text = dateFormat.format(Date(log.loggedAt))
            binding.tvRxNotes.text = log.rxNotes
            binding.tvRxNotes.visibility =
                if (log.rxNotes.isBlank()) android.view.View.GONE else android.view.View.VISIBLE
            binding.root.setOnLongClickListener { onLongClick(log) }
        }
    }

    class FrequencyDiffCallback : DiffUtil.ItemCallback<FrequencyLog>() {
        override fun areItemsTheSame(oldItem: FrequencyLog, newItem: FrequencyLog) =
            oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: FrequencyLog, newItem: FrequencyLog) =
            oldItem == newItem
    }
}
