package com.example.neart_project.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Lift - one logged lift.
 *
 * @Entity tells Room to make a database table called "lifts" from this class.
 * Each property is a column, and each Lift saved is one row.
 *
 * Used by LiftDao (database queries), LiftViewModel (creates lifts) and
 * the History and Log screens (show and edit lifts).
 */
@Entity(tableName = "lifts")
data class Lift(
    // Unique number for each lift. Room picks it automatically (1, 2, 3...) when the lift is saved,
    // so we just pass 0 for a new lift. Room uses this id to know which row to update or delete
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val exercise: String,      // e.g. "Bench Press"
    val muscleGroup: String,   // e.g. "Chest"
    val weightKg: Double,      // Double so weights like 62.5 work
    val sets: Int,
    val reps: Int,
    // When the lift was logged (a timestamp). Set automatically when saved.
    // History uses it to show the newest lifts first
    val date: Long = System.currentTimeMillis()
)
