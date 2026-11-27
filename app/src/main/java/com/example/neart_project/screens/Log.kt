package com.example.neart_project.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.neart_project.R
import com.example.neart_project.data.Lift
import com.example.neart_project.viewmodel.LiftViewModel

/**
 * Log screen - a form to add a new lift or edit an existing one.
 *
 * - liftToEdit is null: empty form, saving adds a new lift
 * - liftToEdit has a lift: form is filled in, saving updates that lift
 *
 * After saving, onSaved() tells MainActivity to go back to History.
 */
@Composable
fun LogScreen(
    viewModel: LiftViewModel,
    onSaved: () -> Unit,          // called after saving
    modifier: Modifier = Modifier,
    liftToEdit: Lift? = null      // the lift tapped on History, or null for a new lift
) {
    // The choices for the dropdowns and chips. Fixed lists keep exercise names
    // spelled the same every time, so personal bests group correctly
    val exercises = listOf("Bench Press", "Squat", "Deadlift", "Overhead Press", "Barbell Row")
    val muscleGroups = listOf("Chest", "Back", "Legs", "Arms")
    val numbers = (1..10).map { it.toString() }   // "1" to "10" for sets and reps

    // What the user has picked or typed so far.
    // If editing, each field starts with the lift's value; otherwise it uses a default (after ?:).
    // rememberSaveable keeps the values when the phone is rotated
    var exercise by rememberSaveable(liftToEdit) { mutableStateOf(liftToEdit?.exercise ?: exercises[0]) }
    var muscleGroup by rememberSaveable(liftToEdit) { mutableStateOf(liftToEdit?.muscleGroup ?: muscleGroups[0]) }
    var weight by rememberSaveable(liftToEdit) { mutableStateOf(liftToEdit?.let { formatWeight(it.weightKg) } ?: "") }
    var sets by rememberSaveable(liftToEdit) { mutableStateOf(liftToEdit?.sets?.toString() ?: "3") }
    var reps by rememberSaveable(liftToEdit) { mutableStateOf(liftToEdit?.reps?.toString() ?: "5") }
    var showError by rememberSaveable(liftToEdit) { mutableStateOf(false) }   // true if the weight is invalid

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())   // lets the form scroll on small screens
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(stringResource(if (liftToEdit != null) R.string.edit_lift else R.string.log_lift), style = MaterialTheme.typography.headlineMedium)

        // Exercise dropdown
        DropdownField(
            label = stringResource(R.string.exercise),
            selected = exercise,
            options = exercises,
            onSelect = { exercise = it },
            modifier = Modifier.fillMaxWidth()
        )

        // Muscle group chips - tap one to select it
        Text(stringResource(R.string.muscle_group), style = MaterialTheme.typography.labelLarge)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            muscleGroups.forEach { group ->
                FilterChip(
                    selected = muscleGroup == group,
                    onClick = { muscleGroup = group },
                    label = { Text(group) }
                )
            }
        }

        // Weight - shows an error if it's empty or not above 0
        OutlinedTextField(
            value = weight,
            onValueChange = {
                weight = it
                showError = false   // hide the error once the user starts typing again
            },
            label = { Text(stringResource(R.string.weight_kg)) },
            isError = showError,
            supportingText = { if (showError) Text(stringResource(R.string.enter_a_weight_above_0)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),   // number keyboard
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        // Sets and reps side by side, each taking half the width
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            DropdownField(
                label = stringResource(R.string.sets),
                selected = sets,
                options = numbers,
                onSelect = { sets = it },
                modifier = Modifier.weight(1f)
            )
            DropdownField(
                label = stringResource(R.string.reps),
                selected = reps,
                options = numbers,
                onSelect = { reps = it },
                modifier = Modifier.weight(1f)
            )
        }

        Button(
            onClick = {
                // Turn the weight text into a number (null if it isn't a valid number)
                val weightKg = weight.toDoubleOrNull()
                if (weightKg == null || weightKg <= 0) {
                    showError = true          // invalid: show error, don't save
                } else {
                    if (liftToEdit != null) {
                        // Editing: copy() keeps the same id and date, and changes the rest.
                        // The id is how the database knows which lift to update
                        viewModel.updateLift(
                            liftToEdit.copy(
                                exercise = exercise,
                                muscleGroup = muscleGroup,
                                weightKg = weightKg,
                                sets = sets.toInt(),
                                reps = reps.toInt()
                            )
                        )
                    } else {
                        // New lift
                        viewModel.addLift(exercise, muscleGroup, weightKg, sets.toInt(), reps.toInt())
                    }
                    weight = ""               // clear the form for next time
                    onSaved()                 // go back to History
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(if (liftToEdit != null) R.string.update_lift else R.string.save_lift))
        }
    }
}

// Reusable dropdown, used for exercise, sets and reps.
// The screen tells it what's selected, and it reports back what the user picks (onSelect)
@Composable
fun DropdownField(
    label: String,
    selected: String,
    options: List<String>,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }   // is the menu open?

    Box(modifier = modifier) {
        // Shows the current choice. readOnly so the user can't type in it
        OutlinedTextField(
            value = selected,
            onValueChange = { },
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { Text("▼") },
            modifier = Modifier.fillMaxWidth()
        )
        // Invisible layer on top so tapping anywhere on the field opens the menu
        Box(
            modifier = Modifier
                .matchParentSize()
                .clickable { expanded = true }
        )
        // The menu of options. Closes when the user taps outside it
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onSelect(option)
                        expanded = false
                    }
                )
            }
        }
    }
}
