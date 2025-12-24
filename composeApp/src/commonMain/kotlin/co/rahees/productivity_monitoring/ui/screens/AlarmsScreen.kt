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
import co.rahees.productivity_monitoring.data.models.Alarm
import kotlinx.datetime.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlarmsScreen() {
    var alarms by remember { 
        mutableStateOf(
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
                )
            )
        )
    }
    var showCreateDialog by remember { mutableStateOf(false) }
    
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
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Smart Alarms",
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
                        contentDescription = "Add Alarm",
                        tint = Color(0xFF6C63FF),
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Alarms list
            if (alarms.isEmpty()) {
                EmptyAlarmsState()
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(alarms) { alarm ->
                        AlarmCard(
                            alarm = alarm,
                            onToggleActive = { id ->
                                alarms = alarms.map {
                                    if (it.id == id) it.copy(isActive = !it.isActive)
                                    else it
                                }
                            },
                            onDelete = { id ->
                                alarms = alarms.filter { it.id != id }
                            }
                        )
                    }
                }
            }
        }
        
        // Create Alarm Dialog
        if (showCreateDialog) {
            CreateAlarmDialog(
                onDismiss = { showCreateDialog = false },
                onConfirm = { newAlarm ->
                    alarms = alarms + newAlarm
                    showCreateDialog = false
                }
            )
        }
    }
}

@Composable
private fun AlarmCard(
    alarm: Alarm,
    onToggleActive: (String) -> Unit,
    onDelete: (String) -> Unit
) {
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
                        text = formatAlarmTime(alarm.time),
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (alarm.isActive) Color.White else Color(0x66FFFFFF)
                        )
                    )
                    
                    Text(
                        text = alarm.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = if (alarm.isActive) Color(0xFF6C63FF) else Color(0x666C63FF),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    
                    if (alarm.description.isNotEmpty()) {
                        Text(
                            text = alarm.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (alarm.isActive) Color(0x99FFFFFF) else Color(0x66FFFFFF),
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                    
                    Row(
                        modifier = Modifier.padding(top = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (alarm.isRecurring) {
                            Icon(
                                imageVector = Icons.Default.Repeat,
                                contentDescription = "Recurring",
                                tint = Color(0xFF03DAC6),
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "Daily",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF03DAC6),
                                modifier = Modifier.padding(start = 4.dp)
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Event,
                                contentDescription = "One time",
                                tint = Color(0xFFFF9800),
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = formatAlarmDate(alarm.time),
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFFFF9800),
                                modifier = Modifier.padding(start = 4.dp)
                            )
                        }
                    }
                }
                
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Switch(
                        checked = alarm.isActive,
                        onCheckedChange = { onToggleActive(alarm.id) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Color(0xFF6C63FF),
                            uncheckedThumbColor = Color(0x66FFFFFF),
                            uncheckedTrackColor = Color(0x33FFFFFF)
                        )
                    )
                    
                    IconButton(
                        onClick = { onDelete(alarm.id) }
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
}

@Composable
private fun EmptyAlarmsState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.AccessTime,
                contentDescription = null,
                tint = Color(0x66FFFFFF),
                modifier = Modifier.size(64.dp)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "No alarms yet",
                style = MaterialTheme.typography.titleLarge,
                color = Color(0x99FFFFFF)
            )
            
            Text(
                text = "Tap + to create your first alarm",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0x66FFFFFF),
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreateAlarmDialog(
    onDismiss: () -> Unit,
    onConfirm: (Alarm) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var isRecurring by remember { mutableStateOf(false) }
    var selectedHour by remember { mutableIntStateOf(8) }
    var selectedMinute by remember { mutableIntStateOf(0) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF1A1A1A),
        title = {
            Text(
                text = "Create Alarm",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Alarm Title", color = Color(0x99FFFFFF)) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF6C63FF),
                        unfocusedBorderColor = Color(0x33FFFFFF),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )
                
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description (Optional)", color = Color(0x99FFFFFF)) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF6C63FF),
                        unfocusedBorderColor = Color(0x33FFFFFF),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )
                
                // Time picker
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0x1AFFFFFF)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Time",
                            color = Color.White,
                            fontWeight = FontWeight.Medium
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Hour picker
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                IconButton(
                                    onClick = { 
                                        selectedHour = if (selectedHour == 23) 0 else selectedHour + 1 
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.KeyboardArrowUp,
                                        contentDescription = "Increase hour",
                                        tint = Color(0xFF6C63FF)
                                    )
                                }
                                
                                Text(
                                    text = String.format("%02d", selectedHour),
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                                
                                IconButton(
                                    onClick = { 
                                        selectedHour = if (selectedHour == 0) 23 else selectedHour - 1 
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.KeyboardArrowDown,
                                        contentDescription = "Decrease hour",
                                        tint = Color(0xFF6C63FF)
                                    )
                                }
                            }
                            
                            Text(
                                text = ":",
                                style = MaterialTheme.typography.headlineSmall,
                                color = Color.White
                            )
                            
                            // Minute picker
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                IconButton(
                                    onClick = { 
                                        selectedMinute = if (selectedMinute == 59) 0 else selectedMinute + 1 
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.KeyboardArrowUp,
                                        contentDescription = "Increase minute",
                                        tint = Color(0xFF6C63FF)
                                    )
                                }
                                
                                Text(
                                    text = String.format("%02d", selectedMinute),
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                                
                                IconButton(
                                    onClick = { 
                                        selectedMinute = if (selectedMinute == 0) 59 else selectedMinute - 1 
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.KeyboardArrowDown,
                                        contentDescription = "Decrease minute",
                                        tint = Color(0xFF6C63FF)
                                    )
                                }
                            }
                        }
                    }
                }
                
                // Recurring toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Repeat Daily",
                        color = Color.White,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    
                    Switch(
                        checked = isRecurring,
                        onCheckedChange = { isRecurring = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Color(0xFF6C63FF),
                            uncheckedThumbColor = Color(0x66FFFFFF),
                            uncheckedTrackColor = Color(0x33FFFFFF)
                        )
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (title.isNotBlank()) {
                        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
                        val alarmTime = now.date.atTime(selectedHour, selectedMinute)
                        
                        val newAlarm = Alarm(
                            id = Clock.System.now().toEpochMilliseconds().toString(),
                            title = title,
                            time = alarmTime,
                            isRecurring = isRecurring,
                            isActive = true,
                            description = description
                        )
                        onConfirm(newAlarm)
                    }
                },
                enabled = title.isNotBlank()
            ) {
                Text(
                    text = "Create",
                    color = if (title.isNotBlank()) Color(0xFF6C63FF) else Color(0x666C63FF)
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

private fun formatAlarmTime(time: LocalDateTime): String {
    return String.format("%02d:%02d", time.hour, time.minute)
}

private fun formatAlarmDate(time: LocalDateTime): String {
    val months = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun",
                       "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
    return "${months[time.monthNumber - 1]} ${time.dayOfMonth}"
}