package com.example.neart_project.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.neart_project.viewmodel.LiftViewModel

@Composable
fun StatsScreen(
    viewModel: LiftViewModel,
    modifier: Modifier = Modifier
) {
    // Live personal bests from the database
    val personalBests by viewModel.personalBests.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Stats", style = MaterialTheme.typography.headlineMedium)

        // Quote of the day card - text will come from the quote API later
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Quote of the day", style = MaterialTheme.typography.labelLarge)
                Text(
                    "\"The only bad workout is the one that didn't happen.\"",
                    style = MaterialTheme.typography.bodyLarge,
                    fontStyle = FontStyle.Italic
                )
            }
        }

        Text("Personal bests", style = MaterialTheme.typography.titleMedium)

        if (personalBests.isEmpty()) {
            Text("Log a lift to see your personal bests.")
        } else {
            // One card per exercise: name on the left, best weight on the right
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) { //LazyColumn only loads items that are visible on the screen
                items(personalBests, key = { it.exercise }) { pb ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween // Exercise on the left, weight on the right
                        ) {
                            Text(pb.exercise, style = MaterialTheme.typography.bodyLarge)
                            Text(
                                "${formatWeight(pb.bestWeight)} kg",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}