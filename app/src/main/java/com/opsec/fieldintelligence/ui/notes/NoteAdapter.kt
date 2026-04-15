package com.opsec.fieldintelligence.ui.notes

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.opsec.fieldintelligence.data.model.Note
import com.opsec.fieldintelligence.databinding.ItemNoteBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NoteAdapter(
    private val onNoteClick: (Note) -> Unit,
    private val onNoteLongClick: (Note) -> Boolean = { false }
) : ListAdapter<Note, NoteAdapter.NoteViewHolder>(NoteDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val binding = ItemNoteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NoteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class NoteViewHolder(private val binding: ItemNoteBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val dateFormat = SimpleDateFormat("MMM d, HH:mm", Locale.getDefault())

        fun bind(note: Note) {
            binding.tvNoteTitle.text = note.title
            binding.tvNoteBody.text = note.body
            binding.tvNoteDate.text = dateFormat.format(Date(note.updatedAt))
            binding.tvNoteCoords.text = "Pin #${note.pinId}"
            binding.root.setOnClickListener { onNoteClick(note) }
            binding.root.setOnLongClickListener { onNoteLongClick(note) }
        }
    }

    class NoteDiffCallback : DiffUtil.ItemCallback<Note>() {
        override fun areItemsTheSame(oldItem: Note, newItem: Note) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Note, newItem: Note) = oldItem == newItem
    }
}
