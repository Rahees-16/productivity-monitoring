package co.rahees.productivity_monitoring.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.rahees.productivity_monitoring.data.models.TodoItem
import co.rahees.productivity_monitoring.domain.usecases.TodoUseCases
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class TodoUiState(
    val todos: List<TodoItem> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val completedTodos: List<TodoItem> = emptyList(),
    val pendingTodos: List<TodoItem> = emptyList(),
    val completionStats: TodoStats = TodoStats()
)

data class TodoStats(
    val totalTasks: Int = 0,
    val completedTasks: Int = 0,
    val pendingTasks: Int = 0,
    val completionPercentage: Int = 0
)

class TodoViewModel(
    private val todoUseCases: TodoUseCases
) : ViewModel() {

    private val _uiState = MutableStateFlow(TodoUiState())
    val uiState: StateFlow<TodoUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<TodoEvent>()
    val events: SharedFlow<TodoEvent> = _events.asSharedFlow()

    init {
        loadTodos()
    }

    fun onAction(action: TodoAction) {
        when (action) {
            is TodoAction.CreateTodo -> createTodo(action.title, action.description)
            is TodoAction.ToggleCompletion -> toggleTodoCompletion(action.id)
            is TodoAction.UpdateTitle -> updateTodoTitle(action.id, action.title)
            is TodoAction.UpdateDescription -> updateTodoDescription(action.id, action.description)
            is TodoAction.DeleteTodo -> deleteTodo(action.id)
            is TodoAction.Retry -> loadTodos()
        }
    }

    private fun loadTodos() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            try {
                todoUseCases.getAllTodos().collect { todos ->
                    val completedTodos = todos.filter { it.isCompleted }
                    val pendingTodos = todos.filter { !it.isCompleted }
                    val stats = TodoStats(
                        totalTasks = todos.size,
                        completedTasks = completedTodos.size,
                        pendingTasks = pendingTodos.size,
                        completionPercentage = if (todos.isNotEmpty()) {
                            (completedTodos.size * 100) / todos.size
                        } else 0
                    )

                    _uiState.update {
                        it.copy(
                            todos = todos,
                            completedTodos = completedTodos,
                            pendingTodos = pendingTodos,
                            completionStats = stats,
                            isLoading = false,
                            error = null
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Unknown error occurred"
                    )
                }
            }
        }
    }

    private fun createTodo(title: String, description: String) {
        viewModelScope.launch {
            todoUseCases.createTodo(title, description)
                .onSuccess {
                    _events.emit(TodoEvent.TodoCreated)
                }
                .onFailure { error ->
                    _events.emit(TodoEvent.Error(error.message ?: "Failed to create todo"))
                }
        }
    }

    private fun toggleTodoCompletion(id: String) {
        viewModelScope.launch {
            todoUseCases.toggleTodoCompletion(id)
                .onSuccess {
                    _events.emit(TodoEvent.TodoUpdated)
                }
                .onFailure { error ->
                    _events.emit(TodoEvent.Error(error.message ?: "Failed to update todo"))
                }
        }
    }

    private fun updateTodoTitle(id: String, title: String) {
        viewModelScope.launch {
            todoUseCases.updateTodoTitle(id, title)
                .onSuccess {
                    _events.emit(TodoEvent.TodoUpdated)
                }
                .onFailure { error ->
                    _events.emit(TodoEvent.Error(error.message ?: "Failed to update todo title"))
                }
        }
    }

    private fun updateTodoDescription(id: String, description: String) {
        viewModelScope.launch {
            todoUseCases.updateTodoDescription(id, description)
                .onSuccess {
                    _events.emit(TodoEvent.TodoUpdated)
                }
                .onFailure { error ->
                    _events.emit(TodoEvent.Error(error.message ?: "Failed to update todo description"))
                }
        }
    }

    private fun deleteTodo(id: String) {
        viewModelScope.launch {
            todoUseCases.deleteTodo(id)
                .onSuccess {
                    _events.emit(TodoEvent.TodoDeleted)
                }
                .onFailure { error ->
                    _events.emit(TodoEvent.Error(error.message ?: "Failed to delete todo"))
                }
        }
    }
}

sealed interface TodoAction {
    data class CreateTodo(val title: String, val description: String = "") : TodoAction
    data class ToggleCompletion(val id: String) : TodoAction
    data class UpdateTitle(val id: String, val title: String) : TodoAction
    data class UpdateDescription(val id: String, val description: String) : TodoAction
    data class DeleteTodo(val id: String) : TodoAction
    object Retry : TodoAction
}

sealed interface TodoEvent {
    object TodoCreated : TodoEvent
    object TodoUpdated : TodoEvent
    object TodoDeleted : TodoEvent
    data class Error(val message: String) : TodoEvent
}