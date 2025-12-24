package co.rahees.productivity_monitoring.data.models

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class TodoItem(
    val id: String,
    val title: String,
    val description: String = "",
    val isCompleted: Boolean = false,
    val createdAt: LocalDateTime,
    val completedAt: LocalDateTime? = null
)

@Serializable
data class Note(
    val id: String,
    val content: String,
    val createdAt: LocalDateTime,
    val lastModified: LocalDateTime
)

@Serializable
data class Alarm(
    val id: String,
    val title: String,
    val time: LocalDateTime,
    val isRecurring: Boolean = false,
    val isActive: Boolean = true,
    val description: String = ""
)

@Serializable
data class PomodoroSession(
    val id: String,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime?,
    val duration: Int, // in minutes
    val isCompleted: Boolean = false,
    val sessionType: SessionType = SessionType.WORK
)

enum class SessionType {
    WORK, SHORT_BREAK, LONG_BREAK
}

@Serializable
data class CalendarEvent(
    val id: String,
    val title: String,
    val date: LocalDate,
    val startTime: LocalDateTime?,
    val endTime: LocalDateTime?,
    val description: String = ""
)