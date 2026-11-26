package com.example.neart_project.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "lifts")
data class Lift(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val exercise: String,
    val muscleGroup: String,
    val weightKg: Double,
    val sets: Int,
    val reps: Int,
    val date: Long = System.currentTimeMillis()  // set automatically when saved
)