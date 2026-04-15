package com.opsec.fieldintelligence.ui.hazard

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.opsec.fieldintelligence.R
import com.opsec.fieldintelligence.data.model.Hazard
import com.opsec.fieldintelligence.data.model.HazardSeverity
import com.opsec.fieldintelligence.databinding.ItemHazardBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HazardAdapter(
    private val onLongClick: (Hazard) -> Boolean = { false }
) : ListAdapter<Hazard, HazardAdapter.HazardViewHolder>(HazardDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HazardViewHolder {
        val binding = ItemHazardBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return HazardViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HazardViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class HazardViewHolder(private val binding: ItemHazardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val dateFormat = SimpleDateFormat("MMM d, HH:mm", Locale.getDefault())

        fun bind(hazard: Hazard) {
            binding.tvSeverity.text = hazard.severity.label.uppercase()
            binding.tvCategory.text = "• ${hazard.category}"
            binding.tvHazardDescription.text = hazard.description
            binding.tvHazardDate.text = dateFormat.format(Date(hazard.reportedAt))

            val severityColor = when (hazard.severity) {
                HazardSeverity.LOW -> R.color.severityLow
                HazardSeverity.MEDIUM -> R.color.severityMedium
                HazardSeverity.HIGH -> R.color.severityHigh
                HazardSeverity.CRITICAL -> R.color.severityCritical
            }
            val color = ContextCompat.getColor(binding.root.context, severityColor)
            binding.severityStripe.setBackgroundColor(color)
            binding.tvSeverity.setTextColor(color)

            binding.root.setOnLongClickListener { onLongClick(hazard) }
        }
    }

    class HazardDiffCallback : DiffUtil.ItemCallback<Hazard>() {
        override fun areItemsTheSame(oldItem: Hazard, newItem: Hazard) =
            oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Hazard, newItem: Hazard) =
            oldItem == newItem
    }
}
