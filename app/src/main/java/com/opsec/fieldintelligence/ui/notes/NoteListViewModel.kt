package com.opsec.fieldintelligence.ui.notes

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.opsec.fieldintelligence.data.db.AppDatabase
import com.opsec.fieldintelligence.data.model.Note
import com.opsec.fieldintelligence.data.repository.NoteRepository
import kotlinx.coroutines.launch

class NoteListViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = NoteRepository(AppDatabase.getInstance(application).noteDao())

    val allNotes: LiveData<List<Note>> = repo.allNotes.asLiveData()

    private val _searchResults = MutableLiveData<List<Note>>()
    val searchResults: LiveData<List<Note>> = _searchResults

    fun search(query: String) = viewModelScope.launch {
        _searchResults.value = repo.search(query)
    }

    fun deleteNote(note: Note) = viewModelScope.launch { repo.deleteNote(note) }

    fun updateNote(note: Note) = viewModelScope.launch { repo.updateNote(note) }
}
