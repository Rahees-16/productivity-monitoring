package co.rahees.productivity_monitoring.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Glassmorphism color palette
private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF6C63FF),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFF4B47CC),
    onPrimaryContainer = Color(0xFFE8E6FF),
    
    secondary = Color(0xFF03DAC6),
    onSecondary = Color(0xFF000000),
    secondaryContainer = Color(0xFF018A80),
    onSecondaryContainer = Color(0xFFB8FFF5),
    
    background = Color(0xFF0A0A0A),
    onBackground = Color(0xFFE8E8E8),
    surface = Color(0x1AFFFFFF),
    onSurface = Color(0xFFE8E8E8),
    surfaceVariant = Color(0x33FFFFFF),
    onSurfaceVariant = Color(0xFFCCCCCC),
    
    error = Color(0xFFFF4444),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFCC1111),
    onErrorContainer = Color(0xFFFFE8E8),
    
    outline = Color(0x66FFFFFF),
    outlineVariant = Color(0x33FFFFFF)
)

@Composable
fun ProductivityTheme(
    darkTheme: Boolean = true, // Always use dark theme for glassmorphism
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}