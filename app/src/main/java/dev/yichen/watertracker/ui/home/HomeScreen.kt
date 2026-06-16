package dev.yichen.watertracker.ui.home

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.yichen.watertracker.domain.model.DrinkEntry
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val QUICK_AMOUNTS = listOf(150, 200, 250, 300)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onOpenSettings: () -> Unit,
    vm: HomeViewModel = viewModel()
) {
    val entries by vm.entries.collectAsState()
    val settings by vm.settings.collectAsState()
    val totalMl = entries.sumOf { it.amountMl }
    val progress = if (settings.goalMl > 0) totalMl.toFloat() / settings.goalMl else 0f

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        val launcher = rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) {}
        LaunchedEffect(settings.reminderEnabled) {
            if (settings.reminderEnabled) {
                launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("WaterTracker") },
                actions = {
                    IconButton(onClick = onOpenSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(16.dp))

            WaterProgressArc(totalMl = totalMl, goalMl = settings.goalMl, progress = progress)

            Spacer(Modifier.height(24.dp))

            Text(
                "Quick Add",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                QUICK_AMOUNTS.forEach { ml ->
                    OutlinedButton(onClick = { vm.addDrink(ml) }) {
                        Text("${ml}ml")
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            if (entries.isEmpty()) {
                Text(
                    "No drinks logged today.\nTap a button above to start!",
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 32.dp)
                )
            } else {
                Text(
                    "Today's log",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.align(Alignment.Start)
                )
                Spacer(Modifier.height(4.dp))
                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    items(entries, key = { it.id }) { entry ->
                        DrinkEntryRow(entry = entry, onDelete = { vm.deleteDrink(entry.id) })
                    }
                }
            }
        }
    }
}

@Composable
private fun WaterProgressArc(totalMl: Int, goalMl: Int, progress: Float) {
    val primary = MaterialTheme.colorScheme.primary
    val surfaceVariant = MaterialTheme.colorScheme.surfaceVariant
    val pct = (progress * 100).toInt().coerceIn(0, 100)

    Box(contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.size(200.dp)) {
            val strokePx = 20.dp.toPx()
            val inset = strokePx / 2f
            val arcSize = Size(size.width - strokePx, size.height - strokePx)
            val topLeft = Offset(inset, inset)
            val startAngle = 150f
            val sweepTotal = 240f

            drawArc(
                color = surfaceVariant,
                startAngle = startAngle,
                sweepAngle = sweepTotal,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(strokePx, cap = StrokeCap.Round)
            )
            drawArc(
                color = primary,
                startAngle = startAngle,
                sweepAngle = sweepTotal * progress.coerceIn(0f, 1f),
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(strokePx, cap = StrokeCap.Round)
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("$totalMl", fontSize = 36.sp, fontWeight = FontWeight.Bold)
            Text("/ $goalMl ml", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("$pct%", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
private fun DrinkEntryRow(entry: DrinkEntry, onDelete: () -> Unit) {
    val fmt = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }
    ListItem(
        headlineContent = { Text("${entry.amountMl} ml") },
        supportingContent = { Text(fmt.format(Date(entry.timestampMs))) },
        trailingContent = {
            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    )
    HorizontalDivider()
}
