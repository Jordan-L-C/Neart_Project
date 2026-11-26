package com.example.neart_project.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

// Result of the personal bests query: one row per exercise
data class PersonalBest(
    val exercise: String,
    val bestWeight: Double
)

@Dao
interface LiftDao {
    // SELECT - newest first. Flow means the list updates automatically when the table changes
    @Query("SELECT * FROM lifts ORDER BY date DESC")
    fun getAllLifts(): Flow<List<Lift>>

    // Heaviest weight for each exercise, for the Stats screen
    @Query("SELECT exercise, MAX(weightKg) AS bestWeight FROM lifts GROUP BY exercise ORDER BY bestWeight DESC")
    fun getPersonalBests(): Flow<List<PersonalBest>>

    @Insert
    suspend fun insert(lift: Lift)

    @Update
    suspend fun update(lift: Lift)

    @Delete
    suspend fun delete(lift: Lift)
}