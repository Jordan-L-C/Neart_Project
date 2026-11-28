package com.example.neart_project.screens

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import com.example.neart_project.R
import kotlinx.coroutines.delay

// Reference: GenAi see prompt(s) (8) in report - whole file

/**
 * RestTimerDialog - a popup that counts down a rest between sets.
 *
 * Opened by MainActivity when ShakeDetector detects a shake.
 * onDismiss is called when the user taps Close (or taps outside the popup),
 * and MainActivity then hides it.
 */
@Composable
fun RestTimerDialog(onDismiss: () -> Unit) {
    // Seconds left on the timer. Starts at 60 every time the dialog opens
    var secondsLeft by remember { mutableIntStateOf(60) }

    // Counts down once a second. LaunchedEffect runs in the background while the dialog
    // is showing, and stops by itself when the dialog closes
    LaunchedEffect(Unit) {
        while (secondsLeft > 0) {
            delay(1000)       // wait 1 second
            secondsLeft--     // the dialog redraws with the new number
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,   // tapping outside the popup closes it
        title = { Text(stringResource(R.string.rest_timer)) },
        text = {
            Text(
                // Show the seconds left, or a message when the time is up
                if (secondsLeft > 0) stringResource(R.string.seconds_left, secondsLeft)
                else stringResource(R.string.rest_over),
                style = MaterialTheme.typography.headlineLarge   // big and easy to read from a distance
            )
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.close))
            }
        }
    )
}
