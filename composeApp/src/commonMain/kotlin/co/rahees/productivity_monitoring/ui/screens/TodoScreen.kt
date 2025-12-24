package co.rahees.productivity_monitoring.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.rahees.productivity_monitoring.data.models.TodoItem
import co.rahees.productivity_monitoring.presentation.viewmodels.TodoAction
import co.rahees.productivity_monitoring.presentation.viewmodels.TodoEvent
import co.rahees.productivity_monitoring.presentation.viewmodels.TodoViewModel
import co.rahees.productivity_monitoring.ui.components.GlassCard
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoScreen(
    viewModel: TodoViewModel = koinInject()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val events by viewModel.events.collectAsStateWithLifecycle(initialValue = null)

    var showCreateDialog by remember { mutableStateOf(false) }

    LaunchedEffect(events) {
        when (events) {
            is TodoEvent.Error -> {
                // Handle error - could show snackbar
            }
            is TodoEvent.TodoCreated -> {
                showCreateDialog = false
            }
            else -> {}
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF1A1A2E),
                        Color(0xFF0A0A0A)
                    ),
                    radius = 1000f
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Header with stats
            TodoStatsHeader(
                stats = uiState.completionStats,
                onCreateClick = { showCreateDialog = true }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Todo List
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            } else {
                val errorMessage = uiState.error
                if (errorMessage != null) {
                    ErrorState(
                        error = errorMessage,
                        onRetry = { viewModel.onAction(TodoAction.Retry) }
                    )
                } else {
                    TodoList(
                        pendingTodos = uiState.pendingTodos,
                        completedTodos = uiState.completedTodos,
                        onToggleCompletion = { id -> viewModel.onAction(TodoAction.ToggleCompletion(id)) },
                        onDeleteTodo = { id -> viewModel.onAction(TodoAction.DeleteTodo(id)) }
                    )
                }
            }
        }

        // Create Todo Dialog
        if (showCreateDialog) {
            CreateTodoDialog(
                onDismiss = { showCreateDialog = false },
                onCreateTodo = { title, description ->
                    viewModel.onAction(TodoAction.CreateTodo(title, description))
                }
            )
        }
    }
}

@Composable
private fun TodoStatsHeader(
    stats: co.rahees.productivity_monitoring.presentation.viewmodels.TodoStats,
    onCreateClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        GlassCard(modifier = Modifier.weight(1f)) {
            Column {
                Text(
                    text = "Progress",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                LinearProgressIndicator(
                    progress = { stats.completionPercentage / 100f },
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = Color.White.copy(alpha = 0.2f)
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "${stats.completedTasks} of ${stats.totalTasks} completed",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }
        }

        Card(
            onClick = onCreateClick,
            modifier = Modifier.size(80.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Todo",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}

@Composable
private fun TodoList(
    pendingTodos: List<TodoItem>,
    completedTodos: List<TodoItem>,
    onToggleCompletion: (String) -> Unit,
    onDeleteTodo: (String) -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (pendingTodos.isNotEmpty()) {
            item {
                Text(
                    text = "Pending Tasks",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            
            items(pendingTodos, key = { it.id }) { todo ->
                TodoItemCard(
                    todo = todo,
                    onToggleCompletion = onToggleCompletion,
                    onDeleteTodo = onDeleteTodo
                )
            }
        }

        if (completedTodos.isNotEmpty()) {
            item {
                Text(
                    text = "Completed Tasks",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White.copy(alpha = 0.7f),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            
            items(completedTodos, key = { it.id }) { todo ->
                TodoItemCard(
                    todo = todo,
                    onToggleCompletion = onToggleCompletion,
                    onDeleteTodo = onDeleteTodo
                )
            }
        }

        if (pendingTodos.isEmpty() && completedTodos.isEmpty()) {
            item {
                GlassCard(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.TaskAlt,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.5f),
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No tasks yet",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                        Text(
                            text = "Add your first task to get started",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.5f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TodoItemCard(
    todo: TodoItem,
    onToggleCompletion: (String) -> Unit,
    onDeleteTodo: (String) -> Unit
) {
    GlassCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = todo.isCompleted,
                onCheckedChange = { onToggleCompletion(todo.id) },
                colors = CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colorScheme.primary,
                    uncheckedColor = Color.White.copy(alpha = 0.5f),
                    checkmarkColor = Color.White
                )
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = todo.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        textDecoration = if (todo.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                    ),
                    color = if (todo.isCompleted) Color.White.copy(alpha = 0.6f) else Color.White,
                    fontWeight = FontWeight.Medium
                )
                
                if (todo.description.isNotEmpty()) {
                    Text(
                        text = todo.description,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            textDecoration = if (todo.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                        ),
                        color = if (todo.isCompleted) Color.White.copy(alpha = 0.4f) else Color.White.copy(alpha = 0.7f)
                    )
                }
            }

            IconButton(
                onClick = { onDeleteTodo(todo.id) }
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = Color.White.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Composable
private fun CreateTodoDialog(
    onDismiss: () -> Unit,
    onCreateTodo: (String, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xDD000000),
        title = {
            Text(
                text = "Create New Task",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title", color = Color.White.copy(alpha = 0.7f)) },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.5f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description (Optional)", color = Color.White.copy(alpha = 0.7f)) },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.5f)
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (title.trim().isNotEmpty()) {
                        onCreateTodo(title.trim(), description.trim())
                    }
                },
                enabled = title.trim().isNotEmpty()
            ) {
                Text(
                    text = "Create",
                    color = MaterialTheme.colorScheme.primary
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "Cancel",
                    color = Color.White.copy(alpha = 0.7f)
                )
            }
        }
    )
}

@Composable
private fun ErrorState(
    error: String,
    onRetry: () -> Unit
) {
    GlassCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.Error,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Something went wrong",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = error,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.7f)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("Retry", color = Color.White)
            }
        }
    }
}