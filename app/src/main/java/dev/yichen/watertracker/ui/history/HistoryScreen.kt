package dev.yichen.watertracker.ui.history

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.yichen.watertracker.domain.GoalCalculator
import dev.yichen.watertracker.domain.model.DrinkEntry
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    onBack: () -> Unit,
    vm: HistoryViewModel = viewModel()
) {
    val days by vm.days.collectAsState()
    val settings by vm.settings.collectAsState()
    val weekStats by vm.weekStats.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("History (7 days)") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        if (days.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "No data yet.\nStart logging drinks on the home screen.",
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    WeekBarChart(days = days, goalMl = settings.goalMl)
                }
                item {
                    StatsCard(stats = weekStats, goalMl = settings.goalMl)
                }
                items(days, key = { it.dayStartMs }) { day ->
                    DayCard(day = day, goalMl = settings.goalMl)
                }
            }
        }
    }
}

@Composable
private fun WeekBarChart(days: List<DayHistory>, goalMl: Int) {
    val today = remember { GoalCalculator.todayStartMs() }
    val dayMap = remember(days) { days.associateBy { it.dayStartMs } }
    val fmt = remember { SimpleDateFormat("EEE", Locale.getDefault()) }

    val bars = remember(days, today) {
        (6 downTo 0).map { daysBack ->
            val dayMs = today - daysBack * 86_400_000L
            Triple(fmt.format(Date(dayMs)), dayMap[dayMs]?.totalMl ?: 0, daysBack == 0)
        }
    }
    val maxMl = remember(bars, goalMl) {
        maxOf(bars.maxOfOrNull { it.second } ?: 0, goalMl, 1)
    }

    val primaryColor = MaterialTheme.colorScheme.primary
    val surfaceColor = MaterialTheme.colorScheme.surfaceVariant
    val goalColor = MaterialTheme.colorScheme.error

    Column(modifier = Modifier.fillMaxWidth()) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
        ) {
            val n = bars.size
            val slotW = size.width / n
            val barW = slotW * 0.55f
            val chartH = size.height

            bars.forEachIndexed { i, (_, totalMl, isToday) ->
                val barH = (totalMl.toFloat() / maxMl * chartH).coerceAtLeast(0f)
                val x = i * slotW + (slotW - barW) / 2f
                drawRect(
                    color = if (isToday) primaryColor else surfaceColor,
                    topLeft = Offset(x, chartH - barH),
                    size = Size(barW, barH)
                )
            }

            if (goalMl > 0) {
                val goalY = chartH - (goalMl.toFloat() / maxMl * chartH)
                drawLine(
                    color = goalColor,
                    start = Offset(0f, goalY.coerceIn(0f, chartH)),
                    end = Offset(size.width, goalY.coerceIn(0f, chartH)),
                    strokeWidth = 1.5.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 4f))
                )
            }
        }

        Row(modifier = Modifier.fillMaxWidth()) {
            bars.forEach { (label, _, isToday) ->
                Text(
                    label,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isToday) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun DayCard(day: DayHistory, goalMl: Int) {
    val progress = if (goalMl > 0) (day.totalMl.toFloat() / goalMl).coerceIn(0f, 1f) else 0f
    val fmt = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }
    val reachedGoal = day.totalMl >= goalMl

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(day.dateLabel, style = MaterialTheme.typography.titleMedium)
                Text(
                    "${day.totalMl} / $goalMl ml",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (reachedGoal) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth()
            )
            if (day.entries.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                HorizontalDivider()
                day.entries.forEach { entry ->
                    EntryRow(entry = entry, fmt = fmt)
                }
            }
        }
    }
}

@Composable
private fun EntryRow(entry: DrinkEntry, fmt: SimpleDateFormat) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            fmt.format(Date(entry.timestampMs)),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            "${entry.drinkType.emoji} ${entry.amountMl} ml",
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
private fun StatsCard(stats: WeekStats, goalMl: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "7-Day Summary",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(
                    label = "Daily avg",
                    value = "${stats.avgMl} ml",
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                StatItem(
                    label = "Goal met",
                    value = "${stats.goalMetDays} / ${stats.totalDays} days",
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                StatItem(
                    label = "Goal",
                    value = "$goalMl ml",
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

@Composable
private fun StatItem(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.bodyMedium, color = color, fontWeight = FontWeight.Bold)
        Text(label, style = MaterialTheme.typography.labelSmall, color = color)
    }
}
