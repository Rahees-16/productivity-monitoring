package co.rahees.productivity_monitoring.ui.shared

import androidx.compose.runtime.*
import co.rahees.productivity_monitoring.data.models.*
import kotlinx.datetime.*

@Stable
class AppState {
    // Todo state
    var todos by mutableStateOf(
        listOf(
            TodoItem(
                id = "1",
                title = "Review project documentation",
                description = "Go through all the technical docs",
                isCompleted = true,
                createdAt = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
                completedAt = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
            ),
            TodoItem(
                id = "2",
                title = "Prepare presentation slides",
                description = "Create slides for the team meeting",
                isCompleted = true,
                createdAt = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
                completedAt = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
            ),
            TodoItem(
                id = "3",
                title = "Update app UI components",
                description = "Improve glassmorphism effects",
                isCompleted = false,
                createdAt = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
            ),
            TodoItem(
                id = "4",
                title = "Write unit tests",
                description = "Add tests for new features",
                isCompleted = false,
                createdAt = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
            ),
            TodoItem(
                id = "5",
                title = "Database optimization",
                description = "Optimize SQL queries",
                isCompleted = false,
                createdAt = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
            )
        )
    )
    
    // Notes state
    var notes by mutableStateOf(
        listOf(
            Note(
                id = "1",
                content = "Remember to review the quarterly goals and prepare the presentation for next week's meeting.",
                createdAt = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
                lastModified = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
            ),
            Note(
                id = "2",
                content = "Ideas for app improvement:\n• Dark mode toggle\n• Export functionality\n• Cloud sync\n• Widgets",
                createdAt = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
                lastModified = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
            ),
            Note(
                id = "3",
                content = "Meeting notes: Discussed new feature requirements and timeline",
                createdAt = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
                lastModified = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
            )
        )
    )
    
    // Alarms state
    var alarms by mutableStateOf(
        listOf(
            Alarm(
                id = "1",
                title = "Morning Workout",
                time = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).let {
                    it.date.atTime(7, 0)
                },
                isRecurring = true,
                isActive = true,
                description = "Time for morning exercise"
            ),
            Alarm(
                id = "2", 
                title = "Team Meeting",
                time = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).let {
                    it.date.atTime(10, 30)
                },
                isRecurring = false,
                isActive = true,
                description = "Weekly team sync"
            ),
            Alarm(
                id = "3", 
                title = "Lunch Break",
                time = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).let {
                    it.date.atTime(12, 30)
                },
                isRecurring = true,
                isActive = false,
                description = "Time for lunch"
            )
        )
    )
    
    // Timer state
    var todayFocusMinutes by mutableIntStateOf(127) // 2h 7min
    var completedPomodoroSessions by mutableIntStateOf(5)
    
    // Computed properties for dashboard
    val totalTodos: Int get() = todos.size
    val completedTodos: Int get() = todos.count { it.isCompleted }
    val totalNotes: Int get() = notes.size
    val activeAlarms: Int get() = alarms.count { it.isActive }
    val completionPercentage: Int get() = if (totalTodos > 0) (completedTodos * 100) / totalTodos else 0
    
    // Actions
    fun addTodo(todo: TodoItem) {
        todos = listOf(todo) + todos
    }
    
    fun updateTodo(todoId: String, updates: (TodoItem) -> TodoItem) {
        todos = todos.map { if (it.id == todoId) updates(it) else it }
    }
    
    fun deleteTodo(todoId: String) {
        todos = todos.filter { it.id != todoId }
    }
    
    fun addNote(note: Note) {
        notes = listOf(note) + notes
    }
    
    fun updateNote(noteId: String, updates: (Note) -> Note) {
        notes = notes.map { if (it.id == noteId) updates(it) else it }
    }
    
    fun deleteNote(noteId: String) {
        notes = notes.filter { it.id != noteId }
    }
    
    fun addAlarm(alarm: Alarm) {
        alarms = alarms + alarm
    }
    
    fun updateAlarm(alarmId: String, updates: (Alarm) -> Alarm) {
        alarms = alarms.map { if (it.id == alarmId) updates(it) else it }
    }
    
    fun deleteAlarm(alarmId: String) {
        alarms = alarms.filter { it.id != alarmId }
    }
    
    fun addFocusTime(minutes: Int) {
        todayFocusMinutes += minutes
    }
    
    fun addPomodoroSession() {
        completedPomodoroSessions += 1
    }
}

@Composable
fun rememberAppState(): AppState {
    return remember { AppState() }
}