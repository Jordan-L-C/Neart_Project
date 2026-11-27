package com.example.neart_project.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * PersonalBest - the result of the personal bests query: an exercise and its heaviest weight.
 * Not a table, just a holder for the query result.
 * The names must match the column names in the query below.
 */
data class PersonalBest(
    val exercise: String,
    val bestWeight: Double
)

/**
 * LiftDao (Data Access Object) - all the ways the app reads and changes the "lifts" table.
 *
 * We only write the function names and SQL here. Room writes the actual code for us when the app builds.
 * Only LiftViewModel uses this - the screens never talk to the database directly.
 */
@Dao
interface LiftDao {

    // Reading: these return a Flow. A Flow sends a new list every time the table changes,
    // so the screens update on their own after a lift is added, edited or deleted.

    // All lifts, newest first (used on History)
    @Query("SELECT * FROM lifts ORDER BY date DESC")
    fun getAllLifts(): Flow<List<Lift>>

    // Heaviest weight for each exercise (used on Stats, and for the PB badge on History).
    // GROUP BY makes one row per exercise, MAX picks the heaviest weight in each
    @Query("SELECT exercise, MAX(weightKg) AS bestWeight FROM lifts GROUP BY exercise ORDER BY bestWeight DESC")
    fun getPersonalBests(): Flow<List<PersonalBest>>

    // Changing data: these are "suspend" functions, meaning they run in the background
    // so the app doesn't freeze. LiftViewModel calls them.

    // Save a new lift
    @Insert
    suspend fun insert(lift: Lift)

    // Save changes to an existing lift (finds it by id)
    @Update
    suspend fun update(lift: Lift)

    // Delete a lift (finds it by id)
    @Delete
    suspend fun delete(lift: Lift)
}
