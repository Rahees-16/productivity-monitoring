package co.rahees.productivity_monitoring.domain.usecases

import co.rahees.productivity_monitoring.data.models.TodoItem
import co.rahees.productivity_monitoring.domain.repositories.TodoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlinx.datetime.format.byUnicodePattern
import kotlinx.datetime.toLocalDateTime
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(FormatStringsInDatetimeFormats::class, ExperimentalUuidApi::class)
class TodoUseCases(private val repository: TodoRepository) {

    private val dateTimeFormatter = LocalDateTime.Format { 
        byUnicodePattern("yyyy-MM-dd'T'HH:mm:ss") 
    }

    suspend fun createTodo(title: String, description: String = ""): Result<Unit> {
        return try {
            val todo = TodoItem(
                id = Uuid.random().toString(),
                title = title.trim(),
                description = description.trim(),
                isCompleted = false,
                createdAt = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
            )
            repository.insertTodo(todo)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun toggleTodoCompletion(id: String): Result<Unit> {
        return try {
            val todo = repository.getTodoById(id)
                ?: return Result.failure(IllegalArgumentException("Todo not found"))
            
            val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
            val completedAt = if (!todo.isCompleted) now.format(dateTimeFormatter) else null
            
            repository.updateTodoCompletion(id, !todo.isCompleted, completedAt)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateTodoTitle(id: String, title: String): Result<Unit> {
        return try {
            if (title.trim().isEmpty()) {
                return Result.failure(IllegalArgumentException("Title cannot be empty"))
            }
            repository.updateTodoTitle(id, title.trim())
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateTodoDescription(id: String, description: String): Result<Unit> {
        return try {
            repository.updateTodoDescription(id, description.trim())
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteTodo(id: String): Result<Unit> {
        return try {
            repository.deleteTodo(id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getAllTodos(): Flow<List<TodoItem>> = repository.getAllTodos()

    suspend fun getTodoById(id: String): TodoItem? = repository.getTodoById(id)
}