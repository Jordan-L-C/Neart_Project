package com.example.neart_project.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.neart_project.R
import com.example.neart_project.data.Lift
import com.example.neart_project.viewmodel.LiftViewModel

/**
 * History screen - a list of every logged lift, newest first.
 *
 * - Lifts that are a personal best get a "PB" badge
 * - Swipe a card left to delete it
 * - Tap a card to edit it (opens the Log screen)
 * - The "+" button logs a new lift
 */

// Shows 100.0 as "100" but keeps 62.5 as "62.5". Also used on the Log and Stats screens
fun formatWeight(weight: Double): String =
    if (weight % 1.0 == 0.0) weight.toInt().toString() else weight.toString()

@Composable
fun HistoryScreen(
    viewModel: LiftViewModel,
    onAddClick: () -> Unit,     // "+" tapped - MainActivity opens the Log screen
    onEdit: (Lift) -> Unit,     // card tapped - MainActivity opens the Log screen with this lift
    modifier: Modifier = Modifier
) {
    // Get the lifts and personal bests from the ViewModel.
    // The screen redraws automatically whenever they change
    val lifts by viewModel.lifts.collectAsStateWithLifecycle()
    val personalBests by viewModel.personalBests.collectAsStateWithLifecycle()

    // Box lets the + button sit on top of the list
    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(stringResource(R.string.history), style = MaterialTheme.typography.headlineMedium)

            if (lifts.isEmpty()) {
                Text(stringResource(R.string.no_lifts_yet_tap_to_log_your_first_lift))
            } else {
                // LazyColumn is a scrolling list that only draws the cards you can see
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    // Space at the bottom so the + button doesn't cover the last card
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    // key = id helps Compose keep track of each card when one is deleted
                    items(lifts, key = { it.id }) { lift ->
                        // It's a PB if it matches the best weight for that exercise
                        val isPb = personalBests.any {
                            it.exercise == lift.exercise && it.bestWeight == lift.weightKg
                        }
                        SwipeableLiftCard(
                            lift, isPb,
                            onDelete = { viewModel.deleteLift(lift) },
                            onClick = { onEdit(lift) }
                        )
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

// One lift card: exercise name and details on the left, PB badge on the right
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
                // e.g. "100 kg · 3x5 · Chest"
                Text(
                    "${formatWeight(lift.weightKg)} kg · ${lift.sets}x${lift.reps} · ${lift.muscleGroup}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            // PB badge, only shown on personal bests
            if (isPb) {
                // Surface = a small coloured box behind the "PB" text
                Surface(
                    // primary is the app's main colour from the theme. Using the strongest colour
                    // makes personal bests stand out from the plain cards around them
                    color = MaterialTheme.colorScheme.primary,
                    shape = MaterialTheme.shapes.small   // rounded corners, from the theme
                ) {
                    Text(
                        // Accessibility: the badge uses the text "PB", not just a colour, so
                        // colour-blind users can still spot it and TalkBack reads it out
                        "PB",
                        // Accessibility: onPrimary is the colour made to go on top of primary - Theme Builder picks the pair so the text always has enough contrast to be readable, in both light and dark mode
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.labelMedium,   // small label text that still scales with the phone's font size
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)   // space around the text inside the badge
                    )
                }
            }
        }
    }
}

// A LiftCard that can be swiped left to delete, or tapped to edit
@Composable
fun SwipeableLiftCard(
    lift: Lift,
    isPb: Boolean,
    onDelete: () -> Unit,
    onClick: () -> Unit
) {
    // Keeps track of the swipe
    val dismissState = rememberSwipeToDismissBoxState()

    // When the card has been swiped all the way left, delete the lift
    LaunchedEffect(dismissState.currentValue) {
        if (dismissState.currentValue == SwipeToDismissBoxValue.EndToStart) {
            onDelete()
        }
    }

    SwipeToDismissBox(
        state = dismissState,
        enableDismissFromStartToEnd = false,   // only allow swiping left
        // Red "Delete" area shown behind the card as it slides away
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    // errorContainer is the theme's soft red, used for warnings and destructive
                    // actions. Red is widely recognised as "danger", so users know this removes the lift
                    .background(MaterialTheme.colorScheme.errorContainer)
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.CenterEnd   // text on the right, where the swipe reveals it
            ) {
                // Accessibility: the word "Delete" is shown, not just the red colour,
                // so colour-blind users still know what the swipe does
                Text(stringResource(R.string.delete))
            }
        }
    ) {
        // The card itself - tapping it opens it for editing
        Box(modifier = Modifier.clickable { onClick() }) {
            LiftCard(lift, isPb)
        }
    }
}
