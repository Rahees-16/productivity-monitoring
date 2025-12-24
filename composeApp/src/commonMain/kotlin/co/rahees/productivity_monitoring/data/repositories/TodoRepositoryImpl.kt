package co.rahees.productivity_monitoring.data.repositories

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import co.rahees.productivity_monitoring.data.models.TodoItem
import co.rahees.productivity_monitoring.database.ProductivityDatabase
import co.rahees.productivity_monitoring.domain.repositories.TodoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.format
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlinx.datetime.format.byUnicodePattern

@OptIn(FormatStringsInDatetimeFormats::class)
class TodoRepositoryImpl(
    private val database: ProductivityDatabase
) : TodoRepository {

    private val dateTimeFormatter = LocalDateTime.Format { 
        byUnicodePattern("yyyy-MM-dd'T'HH:mm:ss") 
    }

    override fun getAllTodos(): Flow<List<TodoItem>> {
        return database.todoItemQueries.selectAll()
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { dbItems ->
                dbItems.map { dbItem ->
                    TodoItem(
                        id = dbItem.id,
                        title = dbItem.title,
                        description = dbItem.description,
                        isCompleted = dbItem.isCompleted == 1L,
                        createdAt = LocalDateTime.parse(dbItem.createdAt, dateTimeFormatter),
                        completedAt = dbItem.completedAt?.let { LocalDateTime.parse(it, dateTimeFormatter) }
                    )
                }
            }
    }

    override suspend fun getTodoById(id: String): TodoItem? = withContext(Dispatchers.Default) {
        val dbItem = database.todoItemQueries.selectById(id).executeAsOneOrNull()
        dbItem?.let {
            TodoItem(
                id = it.id,
                title = it.title,
                description = it.description,
                isCompleted = it.isCompleted == 1L,
                createdAt = LocalDateTime.parse(it.createdAt, dateTimeFormatter),
                completedAt = it.completedAt?.let { completed -> LocalDateTime.parse(completed, dateTimeFormatter) }
            )
        }
    }

    override suspend fun insertTodo(todo: TodoItem) = withContext(Dispatchers.Default) {
        database.todoItemQueries.insert(
            id = todo.id,
            title = todo.title,
            description = todo.description,
            isCompleted = if (todo.isCompleted) 1L else 0L,
            createdAt = todo.createdAt.format(dateTimeFormatter),
            completedAt = todo.completedAt?.format(dateTimeFormatter)
        )
    }

    override suspend fun updateTodoCompletion(id: String, isCompleted: Boolean, completedAt: String?) = withContext(Dispatchers.Default) {
        database.todoItemQueries.updateCompletion(
            isCompleted = if (isCompleted) 1L else 0L,
            completedAt = completedAt,
            id = id
        )
    }

    override suspend fun updateTodoTitle(id: String, title: String) = withContext(Dispatchers.Default) {
        database.todoItemQueries.updateTitle(title, id)
    }

    override suspend fun updateTodoDescription(id: String, description: String) = withContext(Dispatchers.Default) {
        database.todoItemQueries.updateDescription(description, id)
    }

    override suspend fun deleteTodo(id: String) = withContext(Dispatchers.Default) {
        database.todoItemQueries.deleteById(id)
    }
}