package com.opsec.fieldintelligence.data.db.dao

import androidx.room.*
import com.opsec.fieldintelligence.data.model.Note
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(note: Note): Long

    @Update
    suspend fun update(note: Note)

    @Delete
    suspend fun delete(note: Note)

    @Query("SELECT * FROM notes WHERE pinId = :pinId ORDER BY createdAt DESC")
    fun getByPinFlow(pinId: Long): Flow<List<Note>>

    @Query("SELECT * FROM notes ORDER BY updatedAt DESC")
    fun getAllFlow(): Flow<List<Note>>

    @Query("SELECT * FROM notes WHERE title LIKE '%' || :query || '%' OR body LIKE '%' || :query || '%'")
    suspend fun search(query: String): List<Note>

    @Query("SELECT * FROM notes WHERE id = :id")
    suspend fun getById(id: Long): Note?
}
