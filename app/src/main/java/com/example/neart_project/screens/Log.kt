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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.neart_project.viewmodel.LiftViewModel

@Composable
fun LogScreen(
    viewModel: LiftViewModel,
    onSaved: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Fixed lists of choices
    val exercises = listOf("Bench Press", "Squat", "Deadlift", "Overhead Press", "Barbell Row")
    val muscleGroups = listOf("Chest", "Back", "Legs", "Arms")
    val numbers = (1..10).map { it.toString() }

    // State: what the user has picked or typed so far
    var exercise by rememberSaveable { mutableStateOf(exercises[0]) }
    var muscleGroup by rememberSaveable { mutableStateOf(muscleGroups[0]) }
    var weight by rememberSaveable { mutableStateOf("") }
    var sets by rememberSaveable { mutableStateOf("3") }
    var reps by rememberSaveable { mutableStateOf("5") }
    var showError by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Log Lift", style = MaterialTheme.typography.headlineMedium)

        // Exercise dropdown
        DropdownField(
            label = "Exercise",
            selected = exercise,
            options = exercises,
            onSelect = { exercise = it },
            modifier = Modifier.fillMaxWidth()
        )

        // Muscle group chips - tapping one selects it
        Text("Muscle group", style = MaterialTheme.typography.labelLarge)
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
                showError = false
            },
            label = { Text("Weight (kg)") },
            isError = showError,
            supportingText = { if (showError) Text("Enter a weight above 0") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        // Sets and reps side by side
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            DropdownField(
                label = "Sets",
                selected = sets,
                options = numbers,
                onSelect = { sets = it },
                modifier = Modifier.weight(1f)
            )
            DropdownField(
                label = "Reps",
                selected = reps,
                options = numbers,
                onSelect = { reps = it },
                modifier = Modifier.weight(1f)
            )
        }

        Button(
            onClick = {
                val weightKg = weight.toDoubleOrNull()
                if (weightKg == null || weightKg <= 0) {
                    showError = true          // invalid: show error, don't save
                } else {
                    viewModel.addLift(exercise, muscleGroup, weightKg, sets.toInt(), reps.toInt())
                    weight = ""               // clear the form for next time
                    onSaved()                 // go back to History
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save lift")
        }
    }
}

// Reusable dropdown: used for exercise, sets and reps
@Composable
fun DropdownField(
    label: String,
    selected: String,
    options: List<String>,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
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