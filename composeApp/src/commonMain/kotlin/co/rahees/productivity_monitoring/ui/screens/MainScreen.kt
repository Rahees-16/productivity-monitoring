package co.rahees.productivity_monitoring.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.rahees.productivity_monitoring.ui.components.GlassCard
import co.rahees.productivity_monitoring.ui.shared.AppState
import co.rahees.productivity_monitoring.ui.shared.rememberAppState
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    var selectedTab by remember { mutableIntStateOf(0) }
    val appState = rememberAppState()
    
    Scaffold(
        bottomBar = {
            GlassBottomNavigation(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )
        }
    ) { paddingValues ->
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
                .padding(paddingValues)
        ) {
            when (selectedTab) {
                0 -> DashboardContent(
                    appState = appState,
                    onNavigateToTab = { tab -> selectedTab = tab }
                )
                1 -> TimerContent(appState = appState)
                2 -> TodoContent(appState = appState)
                3 -> AlarmsContent(appState = appState)
                4 -> NotesContent(appState = appState)
            }
        }
    }
}

@Composable
private fun GlassBottomNavigation(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    val tabs = listOf(
        BottomNavItem("Dashboard", Icons.Default.Home),
        BottomNavItem("Timer", Icons.Default.Timer),
        BottomNavItem("Tasks", Icons.Default.CheckCircle),
        BottomNavItem("Alarms", Icons.Default.AccessAlarm),
        BottomNavItem("Notes", Icons.Default.Note)
    )
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0x33FFFFFF)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            tabs.forEachIndexed { index, tab ->
                NavigationBarItem(
                    selected = selectedTab == index,
                    onClick = { onTabSelected(index) },
                    icon = {
                        Icon(
                            imageVector = tab.icon,
                            contentDescription = tab.title,
                            tint = if (selectedTab == index) 
                                MaterialTheme.colorScheme.primary 
                            else 
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        indicatorColor = Color.Transparent
                    )
                )
            }
        }
    }
}

@Composable
private fun DashboardContent(
    appState: AppState,
    onNavigateToTab: (Int) -> Unit
) {
    val focusHours = appState.todayFocusMinutes / 60
    val focusMinutes = appState.todayFocusMinutes % 60
    
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Good ${getTimeOfDay()}!",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                    Text(
                        text = "Let's make today productive",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color(0x99FFFFFF)
                    )
                }
                
                // Productivity Score
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0x1AFFFFFF)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Score",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0x99FFFFFF)
                        )
                        Text(
                            text = "${appState.completionPercentage}%",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = when {
                                    appState.completionPercentage >= 80 -> Color(0xFF4CAF50)
                                    appState.completionPercentage >= 60 -> Color(0xFFFF9800)
                                    else -> Color(0xFFFF5252)
                                }
                            )
                        )
                    }
                }
            }
        }
        
        // Statistics Cards
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.Timer,
                    title = "Focus Time",
                    value = if (focusHours > 0) "${focusHours}h ${focusMinutes}m" else "${focusMinutes}m",
                    subtitle = "today",
                    color = Color(0xFF6C63FF),
                    onClick = { onNavigateToTab(1) }
                )
                
                StatCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.CheckCircle,
                    title = "Tasks",
                    value = "${appState.completedTodos}/${appState.totalTodos}",
                    subtitle = "completed",
                    color = Color(0xFF03DAC6),
                    onClick = { onNavigateToTab(2) }
                )
            }
        }
        
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.AccessAlarm,
                    title = "Alarms",
                    value = "${appState.activeAlarms}",
                    subtitle = "active",
                    color = Color(0xFFFF6B9D),
                    onClick = { onNavigateToTab(3) }
                )
                
                StatCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.Note,
                    title = "Notes",
                    value = "${appState.totalNotes}",
                    subtitle = "saved",
                    color = Color(0xFFFF9800),
                    onClick = { onNavigateToTab(4) }
                )
            }
        }
        
        // Progress Overview
        item {
            GlassCard {
                Column {
                    Text(
                        text = "Today's Progress",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Task Progress Bar
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Tasks Completed",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White
                            )
                            Text(
                                text = "${appState.completedTodos} of ${appState.totalTodos}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFF03DAC6),
                                fontWeight = FontWeight.Medium
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        LinearProgressIndicator(
                            progress = { if (appState.totalTodos > 0) appState.completedTodos.toFloat() / appState.totalTodos.toFloat() else 0f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp),
                            color = Color(0xFF03DAC6),
                            trackColor = Color(0x33FFFFFF),
                            strokeCap = StrokeCap.Round
                        )
                    }
                }
            }
        }
        
        // Quick Actions
        item {
            GlassCard {
                Column {
                    Text(
                        text = "Quick Actions",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        QuickActionButton(
                            icon = Icons.Default.PlayArrow,
                            text = "Start Timer",
                            onClick = { onNavigateToTab(1) },
                            modifier = Modifier.weight(1f),
                            color = Color(0xFF6C63FF)
                        )
                        QuickActionButton(
                            icon = Icons.Default.Add,
                            text = "New Task",
                            onClick = { onNavigateToTab(2) },
                            modifier = Modifier.weight(1f),
                            color = Color(0xFF03DAC6)
                        )
                        QuickActionButton(
                            icon = Icons.Default.AccessAlarm,
                            text = "Set Alarm",
                            onClick = { onNavigateToTab(3) },
                            modifier = Modifier.weight(1f),
                            color = Color(0xFFFF6B9D)
                        )
                        QuickActionButton(
                            icon = Icons.Default.Note,
                            text = "Quick Note",
                            onClick = { onNavigateToTab(4) },
                            modifier = Modifier.weight(1f),
                            color = Color(0xFFFF9800)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StatCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    title: String,
    value: String,
    subtitle: String,
    color: Color,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0x1AFFFFFF)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = color
                )
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = Color(0x99FFFFFF)
            )
        }
    }
}

@Composable
private fun QuickActionButton(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0x1AFFFFFF)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.bodySmall,
                color = Color.White,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
private fun TimerContent(appState: AppState) {
    TimerScreen()
}

@Composable
private fun TodoContent(appState: AppState) {
    // For now, use the existing TodoScreen and manually sync data
    // This is a temporary solution until we fully integrate the shared state
    TodoScreen()
}

@Composable
private fun AlarmsContent(appState: AppState) {
    // Use the existing AlarmsScreen
    AlarmsScreen()
}

@Composable
private fun NotesContent(appState: AppState) {
    // Use the existing NotesScreen  
    NotesScreen()
}

private data class BottomNavItem(
    val title: String,
    val icon: ImageVector
)

private fun getTimeOfDay(): String {
    val currentHour = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).hour
    return when (currentHour) {
        in 5..11 -> "morning"
        in 12..17 -> "afternoon"
        in 18..21 -> "evening"
        else -> "night"
    }
}