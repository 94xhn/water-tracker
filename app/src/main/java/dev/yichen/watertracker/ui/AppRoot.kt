package dev.yichen.watertracker.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import dev.yichen.watertracker.ui.history.HistoryScreen
import dev.yichen.watertracker.ui.home.HomeScreen
import dev.yichen.watertracker.ui.settings.SettingsScreen

@Composable
fun AppRoot() {
    var screen by remember { mutableStateOf("home") }
    when (screen) {
        "settings" -> SettingsScreen(onBack = { screen = "home" })
        "history" -> HistoryScreen(onBack = { screen = "home" })
        else -> HomeScreen(
            onOpenSettings = { screen = "settings" },
            onOpenHistory = { screen = "history" }
        )
    }
}
