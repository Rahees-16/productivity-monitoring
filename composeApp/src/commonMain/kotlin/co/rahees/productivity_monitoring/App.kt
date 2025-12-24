package co.rahees.productivity_monitoring

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import co.rahees.productivity_monitoring.ui.screens.MainScreen
import co.rahees.productivity_monitoring.ui.theme.ProductivityTheme

@Composable
fun App() {
    ProductivityTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            MainScreen()
        }
    }
}