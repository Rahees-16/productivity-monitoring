package co.rahees.productivity_monitoring.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.rahees.productivity_monitoring.data.models.Note
import kotlinx.coroutines.delay
import kotlinx.datetime.*
import kotlin.time.Duration.Companion.hours

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesScreen() {
    var notes by remember { 
        mutableStateOf(
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
                    createdAt = Clock.System.now().minus(1.hours).toLocalDateTime(TimeZone.currentSystemDefault()),
                    lastModified = Clock.System.now().minus(1.hours).toLocalDateTime(TimeZone.currentSystemDefault())
                )
            )
        )
    }
    var showCreateDialog by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    
    val filteredNotes = notes.filter {
        it.content.contains(searchQuery, ignoreCase = true)
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF0A0A0A),
                        Color(0xFF1A1A1A),
                        Color(0xFF0A0A0A)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Header with search
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Quick Notes",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
                
                IconButton(
                    onClick = { showCreateDialog = true }
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Note",
                        tint = Color(0xFF6C63FF),
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
            
            // Search bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                placeholder = { 
                    Text(
                        "Search notes...", 
                        color = Color(0x66FFFFFF)
                    ) 
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = Color(0x99FFFFFF)
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(
                            onClick = { searchQuery = "" }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear",
                                tint = Color(0x99FFFFFF)
                            )
                        }
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF6C63FF),
                    unfocusedBorderColor = Color(0x33FFFFFF),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                shape = RoundedCornerShape(16.dp)
            )
            
            // Notes list
            if (filteredNotes.isEmpty()) {
                if (searchQuery.isEmpty()) {
                    EmptyNotesState()
                } else {
                    NoSearchResultsState()
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredNotes) { note ->
                        NoteCard(
                            note = note,
                            onEdit = { editedNote ->
                                notes = notes.map {
                                    if (it.id == editedNote.id) editedNote
                                    else it
                                }
                            },
                            onDelete = { id ->
                                notes = notes.filter { it.id != id }
                            }
                        )
                    }
                }
            }
        }
        
        // Create Note Dialog
        if (showCreateDialog) {
            CreateNoteDialog(
                onDismiss = { showCreateDialog = false },
                onConfirm = { newNote ->
                    notes = listOf(newNote) + notes
                    showCreateDialog = false
                }
            )
        }
    }
}

@Composable
private fun NoteCard(
    note: Note,
    onEdit: (Note) -> Unit,
    onDelete: (String) -> Unit
) {
    var showEditDialog by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0x1AFFFFFF)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = note.content,
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White,
                        lineHeight = 24.sp
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = null,
                            tint = Color(0x66FFFFFF),
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = formatNoteTime(note.lastModified),
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0x66FFFFFF),
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                }
                
                Row {
                    IconButton(
                        onClick = { showEditDialog = true }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = Color(0xFF03DAC6),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    
                    IconButton(
                        onClick = { onDelete(note.id) }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = Color(0xFFFF5252),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
    
    // Edit Dialog
    if (showEditDialog) {
        EditNoteDialog(
            note = note,
            onDismiss = { showEditDialog = false },
            onConfirm = { editedNote ->
                onEdit(editedNote)
                showEditDialog = false
            }
        )
    }
}

@Composable
private fun EmptyNotesState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Note,
                contentDescription = null,
                tint = Color(0x66FFFFFF),
                modifier = Modifier.size(64.dp)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "No notes yet",
                style = MaterialTheme.typography.titleLarge,
                color = Color(0x99FFFFFF)
            )
            
            Text(
                text = "Tap + to create your first note",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0x66FFFFFF),
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Composable
private fun NoSearchResultsState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.SearchOff,
                contentDescription = null,
                tint = Color(0x66FFFFFF),
                modifier = Modifier.size(64.dp)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "No notes found",
                style = MaterialTheme.typography.titleLarge,
                color = Color(0x99FFFFFF)
            )
            
            Text(
                text = "Try a different search term",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0x66FFFFFF),
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreateNoteDialog(
    onDismiss: () -> Unit,
    onConfirm: (Note) -> Unit
) {
    var content by remember { mutableStateOf("") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF1A1A1A),
        title = {
            Text(
                text = "Create Note",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 120.dp),
                placeholder = { 
                    Text(
                        "Write your note...", 
                        color = Color(0x66FFFFFF)
                    ) 
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF6C63FF),
                    unfocusedBorderColor = Color(0x33FFFFFF),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                maxLines = 8
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (content.isNotBlank()) {
                        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
                        val newNote = Note(
                            id = Clock.System.now().toEpochMilliseconds().toString(),
                            content = content,
                            createdAt = now,
                            lastModified = now
                        )
                        onConfirm(newNote)
                    }
                },
                enabled = content.isNotBlank()
            ) {
                Text(
                    text = "Save",
                    color = if (content.isNotBlank()) Color(0xFF6C63FF) else Color(0x666C63FF)
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text(
                    text = "Cancel",
                    color = Color(0x99FFFFFF)
                )
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditNoteDialog(
    note: Note,
    onDismiss: () -> Unit,
    onConfirm: (Note) -> Unit
) {
    var content by remember { mutableStateOf(note.content) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF1A1A1A),
        title = {
            Text(
                text = "Edit Note",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 120.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF6C63FF),
                    unfocusedBorderColor = Color(0x33FFFFFF),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                maxLines = 8
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (content.isNotBlank()) {
                        val editedNote = note.copy(
                            content = content,
                            lastModified = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
                        )
                        onConfirm(editedNote)
                    }
                },
                enabled = content.isNotBlank()
            ) {
                Text(
                    text = "Update",
                    color = if (content.isNotBlank()) Color(0xFF6C63FF) else Color(0x666C63FF)
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text(
                    text = "Cancel",
                    color = Color(0x99FFFFFF)
                )
            }
        }
    )
}

private fun formatNoteTime(time: LocalDateTime): String {
    val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    val today = now.date
    val yesterday = today.minus(1, DateTimeUnit.DAY)
    
    return when (time.date) {
        today -> "Today ${String.format("%02d:%02d", time.hour, time.minute)}"
        yesterday -> "Yesterday ${String.format("%02d:%02d", time.hour, time.minute)}"
        else -> {
            val months = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun",
                              "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
            "${months[time.monthNumber - 1]} ${time.dayOfMonth}"
        }
    }
}