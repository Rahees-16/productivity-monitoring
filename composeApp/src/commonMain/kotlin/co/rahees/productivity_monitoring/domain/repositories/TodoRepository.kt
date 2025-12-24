package co.rahees.productivity_monitoring.domain.repositories

import co.rahees.productivity_monitoring.data.models.TodoItem
import kotlinx.coroutines.flow.Flow

interface TodoRepository {
    fun getAllTodos(): Flow<List<TodoItem>>
    suspend fun getTodoById(id: String): TodoItem?
    suspend fun insertTodo(todo: TodoItem)
    suspend fun updateTodoCompletion(id: String, isCompleted: Boolean, completedAt: String?)
    suspend fun updateTodoTitle(id: String, title: String)
    suspend fun updateTodoDescription(id: String, description: String)
    suspend fun deleteTodo(id: String)
}