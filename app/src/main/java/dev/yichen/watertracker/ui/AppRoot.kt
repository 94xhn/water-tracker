package dev.yichen.watertracker.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import dev.yichen.watertracker.ui.home.HomeScreen
import dev.yichen.watertracker.ui.settings.SettingsScreen

@Composable
fun AppRoot() {
    var showSettings by remember { mutableStateOf(false) }
    if (showSettings) {
        SettingsScreen(onBack = { showSettings = false })
    } else {
        HomeScreen(onOpenSettings = { showSettings = true })
    }
}
