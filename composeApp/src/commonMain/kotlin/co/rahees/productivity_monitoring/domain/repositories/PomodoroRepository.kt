package co.rahees.productivity_monitoring.domain.repositories

import co.rahees.productivity_monitoring.data.models.PomodoroSession
import kotlinx.coroutines.flow.Flow

interface PomodoroRepository {
    fun getAllSessions(): Flow<List<PomodoroSession>>
    fun getSessionsByDate(date: String): Flow<List<PomodoroSession>>
    suspend fun getSessionById(id: String): PomodoroSession?
    suspend fun insertSession(session: PomodoroSession)
    suspend fun updateSessionCompletion(id: String, isCompleted: Boolean, endTime: String?)
    suspend fun deleteSession(id: String)
}