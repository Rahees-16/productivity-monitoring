package co.rahees.productivity_monitoring.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import co.rahees.productivity_monitoring.data.models.SessionType
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.PI

@Composable
fun TimerScreen() {
    var timeLeft by remember { mutableIntStateOf(25 * 60) } // 25 minutes in seconds
    var totalTime by remember { mutableIntStateOf(25 * 60) }
    var isRunning by remember { mutableStateOf(false) }
    var currentSession by remember { mutableStateOf(SessionType.WORK) }
    var showSettings by remember { mutableStateOf(false) }
    
    // Timer settings
    var workDuration by remember { mutableIntStateOf(25) }
    var shortBreakDuration by remember { mutableIntStateOf(5) }
    var longBreakDuration by remember { mutableIntStateOf(15) }
    var sessionsUntilLongBreak by remember { mutableIntStateOf(4) }
    var currentSessionCount by remember { mutableIntStateOf(0) }
    
    // Animation
    val scale by animateFloatAsState(
        targetValue = if (isRunning) 1.05f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "timer_pulse"
    )
    
    // Timer logic
    LaunchedEffect(isRunning) {
        if (isRunning && timeLeft > 0) {
            while (isRunning && timeLeft > 0) {
                delay(1000)
                timeLeft--
            }
            if (timeLeft == 0) {
                isRunning = false
                // Session completed logic
                when (currentSession) {
                    SessionType.WORK -> {
                        currentSessionCount++
                        if (currentSessionCount % sessionsUntilLongBreak == 0) {
                            currentSession = SessionType.LONG_BREAK
                            timeLeft = longBreakDuration * 60
                            totalTime = longBreakDuration * 60
                        } else {
                            currentSession = SessionType.SHORT_BREAK
                            timeLeft = shortBreakDuration * 60
                            totalTime = shortBreakDuration * 60
                        }
                    }
                    SessionType.SHORT_BREAK, SessionType.LONG_BREAK -> {
                        currentSession = SessionType.WORK
                        timeLeft = workDuration * 60
                        totalTime = workDuration * 60
                    }
                }
            }
        }
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
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Session type indicator
            Card(
                modifier = Modifier.padding(bottom = 32.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0x1AFFFFFF)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = when (currentSession) {
                        SessionType.WORK -> "Focus Time"
                        SessionType.SHORT_BREAK -> "Short Break"
                        SessionType.LONG_BREAK -> "Long Break"
                    },
                    modifier = Modifier.padding(16.dp, 8.dp),
                    style = MaterialTheme.typography.titleMedium,
                    color = when (currentSession) {
                        SessionType.WORK -> Color(0xFF6C63FF)
                        SessionType.SHORT_BREAK -> Color(0xFF03DAC6)
                        SessionType.LONG_BREAK -> Color(0xFFFF6B9D)
                    }
                )
            }
            
            // Timer circle
            Box(
                modifier = Modifier
                    .size(280.dp)
                    .scale(if (isRunning) scale else 1f),
                contentAlignment = Alignment.Center
            ) {
                // Progress circle
                Canvas(
                    modifier = Modifier.fillMaxSize()
                ) {
                    val progress = if (totalTime > 0) {
                        (totalTime - timeLeft).toFloat() / totalTime.toFloat()
                    } else 0f
                    
                    val strokeWidth = 12.dp.toPx()
                    val radius = (size.minDimension - strokeWidth) / 2
                    val center = androidx.compose.ui.geometry.Offset(
                        size.width / 2f,
                        size.height / 2f
                    )
                    
                    // Background circle
                    drawCircle(
                        color = Color(0x33FFFFFF),
                        radius = radius,
                        center = center,
                        style = Stroke(width = strokeWidth)
                    )
                    
                    // Progress arc
                    val sweepAngle = progress * 360f
                    if (sweepAngle > 0f) {
                        drawArc(
                            color = when (currentSession) {
                                SessionType.WORK -> Color(0xFF6C63FF)
                                SessionType.SHORT_BREAK -> Color(0xFF03DAC6)
                                SessionType.LONG_BREAK -> Color(0xFFFF6B9D)
                            },
                            startAngle = -90f,
                            sweepAngle = sweepAngle,
                            useCenter = false,
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                            topLeft = androidx.compose.ui.geometry.Offset(
                                center.x - radius,
                                center.y - radius
                            ),
                            size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2)
                        )
                    }
                }
                
                // Time display
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = formatTime(timeLeft),
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Light,
                        color = Color.White
                    )
                    Text(
                        text = "Session ${currentSessionCount + 1}",
                        fontSize = 14.sp,
                        color = Color(0x99FFFFFF),
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(48.dp))
            
            // Control buttons
            Row(
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Play/Pause button
                FloatingActionButton(
                    onClick = { isRunning = !isRunning },
                    modifier = Modifier.size(64.dp),
                    containerColor = when (currentSession) {
                        SessionType.WORK -> Color(0xFF6C63FF)
                        SessionType.SHORT_BREAK -> Color(0xFF03DAC6)
                        SessionType.LONG_BREAK -> Color(0xFFFF6B9D)
                    }
                ) {
                    Icon(
                        imageVector = if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isRunning) "Pause" else "Start",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
                
                // Reset button
                FloatingActionButton(
                    onClick = {
                        isRunning = false
                        timeLeft = when (currentSession) {
                            SessionType.WORK -> workDuration * 60
                            SessionType.SHORT_BREAK -> shortBreakDuration * 60
                            SessionType.LONG_BREAK -> longBreakDuration * 60
                        }
                        totalTime = timeLeft
                    },
                    modifier = Modifier.size(48.dp),
                    containerColor = Color(0x33FFFFFF)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Reset",
                        tint = Color.White
                    )
                }
                
                // Settings button
                FloatingActionButton(
                    onClick = { showSettings = true },
                    modifier = Modifier.size(48.dp),
                    containerColor = Color(0x33FFFFFF)
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = Color.White
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Progress info
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color(0x1AFFFFFF)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Completed",
                            fontSize = 12.sp,
                            color = Color(0x99FFFFFF)
                        )
                        Text(
                            text = "$currentSessionCount",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF6C63FF)
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Until Long Break",
                            fontSize = 12.sp,
                            color = Color(0x99FFFFFF)
                        )
                        Text(
                            text = "${sessionsUntilLongBreak - (currentSessionCount % sessionsUntilLongBreak)}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF03DAC6)
                        )
                    }
                }
            }
        }
        
        // Settings Dialog
        if (showSettings) {
            TimerSettingsDialog(
                workDuration = workDuration,
                shortBreakDuration = shortBreakDuration,
                longBreakDuration = longBreakDuration,
                sessionsUntilLongBreak = sessionsUntilLongBreak,
                onWorkDurationChange = { workDuration = it },
                onShortBreakDurationChange = { shortBreakDuration = it },
                onLongBreakDurationChange = { longBreakDuration = it },
                onSessionsUntilLongBreakChange = { sessionsUntilLongBreak = it },
                onDismiss = { showSettings = false },
                onApply = { 
                    showSettings = false
                    if (!isRunning) {
                        timeLeft = when (currentSession) {
                            SessionType.WORK -> workDuration * 60
                            SessionType.SHORT_BREAK -> shortBreakDuration * 60
                            SessionType.LONG_BREAK -> longBreakDuration * 60
                        }
                        totalTime = timeLeft
                    }
                }
            )
        }
    }
}

@Composable
private fun TimerSettingsDialog(
    workDuration: Int,
    shortBreakDuration: Int,
    longBreakDuration: Int,
    sessionsUntilLongBreak: Int,
    onWorkDurationChange: (Int) -> Unit,
    onShortBreakDurationChange: (Int) -> Unit,
    onLongBreakDurationChange: (Int) -> Unit,
    onSessionsUntilLongBreakChange: (Int) -> Unit,
    onDismiss: () -> Unit,
    onApply: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF1A1A1A),
        title = {
            Text(
                text = "Timer Settings",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                SettingSlider(
                    label = "Work Duration",
                    value = workDuration,
                    range = 5..60,
                    onValueChange = onWorkDurationChange,
                    unit = "min"
                )
                SettingSlider(
                    label = "Short Break",
                    value = shortBreakDuration,
                    range = 1..15,
                    onValueChange = onShortBreakDurationChange,
                    unit = "min"
                )
                SettingSlider(
                    label = "Long Break",
                    value = longBreakDuration,
                    range = 10..30,
                    onValueChange = onLongBreakDurationChange,
                    unit = "min"
                )
                SettingSlider(
                    label = "Sessions Until Long Break",
                    value = sessionsUntilLongBreak,
                    range = 2..8,
                    onValueChange = onSessionsUntilLongBreakChange,
                    unit = ""
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = onApply
            ) {
                Text(
                    text = "Apply",
                    color = Color(0xFF6C63FF)
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

@Composable
private fun SettingSlider(
    label: String,
    value: Int,
    range: IntRange,
    onValueChange: (Int) -> Unit,
    unit: String
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                color = Color.White,
                fontSize = 14.sp
            )
            Text(
                text = "$value $unit",
                color = Color(0xFF6C63FF),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
        Slider(
            value = value.toFloat(),
            onValueChange = { onValueChange(it.toInt()) },
            valueRange = range.first.toFloat()..range.last.toFloat(),
            colors = SliderDefaults.colors(
                thumbColor = Color(0xFF6C63FF),
                activeTrackColor = Color(0xFF6C63FF),
                inactiveTrackColor = Color(0x33FFFFFF)
            )
        )
    }
}

private fun formatTime(seconds: Int): String {
    val minutes = seconds / 60
    val remainingSeconds = seconds % 60
    return String.format("%02d:%02d", minutes, remainingSeconds)
}