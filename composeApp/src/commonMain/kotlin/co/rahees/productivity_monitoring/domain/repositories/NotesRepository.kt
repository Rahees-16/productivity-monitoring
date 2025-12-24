package co.rahees.productivity_monitoring.domain.repositories

import co.rahees.productivity_monitoring.data.models.Note
import kotlinx.coroutines.flow.Flow

interface NotesRepository {
    fun getAllNotes(): Flow<List<Note>>
    suspend fun getNoteById(id: String): Note?
    suspend fun insertNote(note: Note)
    suspend fun updateNoteContent(id: String, content: String, lastModified: String)
    suspend fun deleteNote(id: String)
}