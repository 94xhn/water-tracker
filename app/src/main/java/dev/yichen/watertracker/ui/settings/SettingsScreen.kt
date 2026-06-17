package dev.yichen.watertracker.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.yichen.watertracker.domain.GoalCalculator
import dev.yichen.watertracker.domain.model.Settings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    vm: SettingsViewModel = viewModel()
) {
    val saved by vm.settings.collectAsState()
    val customDrinks by vm.customDrinks.collectAsState()
    val context = LocalContext.current

    var goalText by remember(saved.goalMl) { mutableStateOf(saved.goalMl.toString()) }
    var weightText by remember(saved.weightKg) {
        mutableStateOf(if (saved.weightKg > 0) saved.weightKg.toString() else "")
    }
    var reminderEnabled by remember(saved.reminderEnabled) { mutableStateOf(saved.reminderEnabled) }
    var startHour by remember(saved.reminderStartHour) { mutableIntStateOf(saved.reminderStartHour) }
    var endHour by remember(saved.reminderEndHour) { mutableIntStateOf(saved.reminderEndHour) }
    var intervalHours by remember(saved.reminderIntervalHours) { mutableIntStateOf(saved.reminderIntervalHours) }
    var cup1 by remember(saved.cupSizes) { mutableStateOf(saved.cupSizes.getOrElse(0) { 150 }.toString()) }
    var cup2 by remember(saved.cupSizes) { mutableStateOf(saved.cupSizes.getOrElse(1) { 200 }.toString()) }
    var cup3 by remember(saved.cupSizes) { mutableStateOf(saved.cupSizes.getOrElse(2) { 250 }.toString()) }
    var cup4 by remember(saved.cupSizes) { mutableStateOf(saved.cupSizes.getOrElse(3) { 300 }.toString()) }

    val weightKg = weightText.toFloatOrNull() ?: 0f
    val recommendedMl = if (weightKg > 0) GoalCalculator.recommendedMl(weightKg) else null

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = goalText,
                onValueChange = { goalText = it },
                label = { Text("Daily goal (ml)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = weightText,
                onValueChange = { weightText = it },
                label = { Text("Weight kg (optional)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                supportingText = recommendedMl?.let { { Text("Recommended: $it ml/day") } },
                modifier = Modifier.fillMaxWidth()
            )
            if (recommendedMl != null) {
                TextButton(onClick = { goalText = recommendedMl.toString() }) {
                    Text("Use recommended ($recommendedMl ml)")
                }
            }

            HorizontalDivider()

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Reminders", modifier = Modifier.weight(1f))
                Switch(checked = reminderEnabled, onCheckedChange = { reminderEnabled = it })
            }

            if (reminderEnabled) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = startHour.toString(),
                        onValueChange = { it.toIntOrNull()?.let { h -> if (h in 0..23) startHour = h } },
                        label = { Text("Start hour") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = endHour.toString(),
                        onValueChange = { it.toIntOrNull()?.let { h -> if (h in 0..23) endHour = h } },
                        label = { Text("End hour") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                }

                Text("Remind every:", style = MaterialTheme.typography.labelMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(1, 2, 3).forEach { h ->
                        FilterChip(
                            selected = intervalHours == h,
                            onClick = { intervalHours = h },
                            label = { Text("${h}h") }
                        )
                    }
                }
            }

            HorizontalDivider()

            Text("Quick Add cup sizes (ml)", style = MaterialTheme.typography.labelMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(cup1 to { v: String -> cup1 = v },
                       cup2 to { v: String -> cup2 = v },
                       cup3 to { v: String -> cup3 = v },
                       cup4 to { v: String -> cup4 = v })
                    .forEachIndexed { idx, (text, setter) ->
                        OutlinedTextField(
                            value = text,
                            onValueChange = setter,
                            label = { Text("Cup ${idx + 1}") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }
            }

            HorizontalDivider()

            Text("Custom Drink Types", style = MaterialTheme.typography.labelMedium)

            if (customDrinks.isNotEmpty()) {
                customDrinks.forEach { drink ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "${drink.emoji} ${drink.displayName}  ×${drink.hydrationFactor}",
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.bodyMedium
                        )
                        IconButton(onClick = { vm.deleteCustomDrink(drink.customId) }) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Delete",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }

            var newName by remember { mutableStateOf("") }
            var newEmoji by remember { mutableStateOf("💧") }
            var newFactor by remember { mutableStateOf("1.0") }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = newEmoji,
                    onValueChange = { if (it.length <= 2) newEmoji = it },
                    label = { Text("Emoji") },
                    singleLine = true,
                    modifier = Modifier.width(72.dp)
                )
                OutlinedTextField(
                    value = newName,
                    onValueChange = { newName = it },
                    label = { Text("Name") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = newFactor,
                    onValueChange = { newFactor = it },
                    label = { Text("×Factor") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.width(90.dp)
                )
            }
            OutlinedButton(
                onClick = {
                    val factor = newFactor.toFloatOrNull()?.coerceIn(0.1f, 1.5f) ?: 1.0f
                    if (newName.isNotBlank()) {
                        vm.addCustomDrink(newName.trim(), newEmoji.ifBlank { "💧" }, factor)
                        newName = ""; newEmoji = "💧"; newFactor = "1.0"
                    }
                },
                enabled = newName.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("+ Add drink type")
            }

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = {
                    fun parseCup(s: String, default: Int) =
                        s.toIntOrNull()?.coerceIn(10, 2000) ?: default
                    vm.save(
                        Settings(
                            goalMl = goalText.toIntOrNull()?.coerceIn(100, 10000) ?: 2000,
                            weightKg = weightKg,
                            reminderEnabled = reminderEnabled,
                            reminderStartHour = startHour,
                            reminderEndHour = endHour,
                            reminderIntervalHours = intervalHours,
                            cupSizes = listOf(
                                parseCup(cup1, 150), parseCup(cup2, 200),
                                parseCup(cup3, 250), parseCup(cup4, 300)
                            )
                        )
                    )
                    onBack()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save")
            }

            OutlinedButton(
                onClick = { vm.exportCsv(context) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Export all data as CSV")
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}
