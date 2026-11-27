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

/**
 * Stats screen - a quote of the day and your personal best for each exercise.
 *
 * Personal bests come from the ViewModel, so this list updates by itself
 * when lifts are added, edited or deleted on the other screens.
 */
@Composable
fun StatsScreen(
    viewModel: LiftViewModel,
    modifier: Modifier = Modifier
) {
    // Get the personal bests from the ViewModel (redraws when they change)
    val personalBests by viewModel.personalBests.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Stats", style = MaterialTheme.typography.headlineMedium)

        // Reference: GenAi see prompt(s) (6) in report - quote card below
        // Quote of the day from the API (see QuoteApi). Null while loading or with no internet
        val quote = viewModel.quote

        // Quote of the day card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Quote of the day", style = MaterialTheme.typography.labelLarge)
                Text(
                    // Show the API quote, or a default one if it hasn't loaded
                    "\"${quote?.q ?: "The only bad workout is the one that didn't happen."}\"",
                    style = MaterialTheme.typography.bodyLarge,
                    fontStyle = FontStyle.Italic
                )
                // Author, only shown when the quote came from the API
                if (quote != null) {
                    Text("- ${quote.a}", style = MaterialTheme.typography.bodyMedium)
                }
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
