package com.opsec.fieldintelligence.data.repository

import com.opsec.fieldintelligence.data.db.dao.NoteDao
import com.opsec.fieldintelligence.data.model.Note
import kotlinx.coroutines.flow.Flow

class NoteRepository(private val dao: NoteDao) {

    val allNotes: Flow<List<Note>> = dao.getAllFlow()

    fun notesForPin(pinId: Long): Flow<List<Note>> = dao.getByPinFlow(pinId)

    suspend fun addNote(note: Note): Long = dao.insert(note)

    suspend fun updateNote(note: Note) = dao.update(note)

    suspend fun deleteNote(note: Note) = dao.delete(note)

    suspend fun getNoteById(id: Long): Note? = dao.getById(id)

    suspend fun search(query: String): List<Note> = dao.search(query)
}
