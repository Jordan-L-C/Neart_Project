package com.example.neart_project.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.neart_project.data.Lift
import com.example.neart_project.viewmodel.LiftViewModel

// Shows 100.0 as "100" but keeps 62.5 as "62.5"
fun formatWeight(weight: Double): String =
    if (weight % 1.0 == 0.0) weight.toInt().toString() else weight.toString()

@Composable
fun HistoryScreen(
    viewModel: LiftViewModel,
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Live data from the database - the screen redraws whenever it changes
    val lifts by viewModel.lifts.collectAsStateWithLifecycle()
    val personalBests by viewModel.personalBests.collectAsStateWithLifecycle()

    // Box lets the + button sit on top of the list
    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("History", style = MaterialTheme.typography.headlineMedium)

            if (lifts.isEmpty()) {
                Text("No lifts yet. Tap + to log your first lift.")
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    // Space at the bottom so the + button doesn't cover the last card
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    // key = id so Compose tracks each lift correctly when the list changes
                    items(lifts, key = { it.id }) { lift ->
                        // It's a PB if it matches the best weight for that exercise
                        val isPb = personalBests.any {
                            it.exercise == lift.exercise && it.bestWeight == lift.weightKg
                        }
                        LiftCard(lift, isPb)
                    }
                }
            }
        }

        // + button in the bottom-right corner, opens the Log screen
        FloatingActionButton(
            onClick = onAddClick,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Text("+", style = MaterialTheme.typography.headlineSmall)
        }
    }
}

// One lift in the list
@Composable
fun LiftCard(lift: Lift, isPb: Boolean) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(lift.exercise, style = MaterialTheme.typography.titleMedium)
                Text(
                    "${formatWeight(lift.weightKg)} kg · ${lift.sets}x${lift.reps} · ${lift.muscleGroup}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            // PB badge, only shown on personal bests
            if (isPb) {
                Surface(
                    color = MaterialTheme.colorScheme.primary,
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        "PB",
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}