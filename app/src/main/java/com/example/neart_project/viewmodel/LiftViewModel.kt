package com.example.neart_project.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.neart_project.data.AppDatabase
import com.example.neart_project.data.Lift
import com.example.neart_project.data.PersonalBest
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * LiftViewModel - the middle layer between the screens and the database (MVVM).
 *
 *   Screens  ->  LiftViewModel  ->  LiftDao  ->  database
 *
 * The screens read lifts and personal bests from here, and call addLift, updateLift
 * and deleteLift to make changes. They never touch the database themselves.
 *
 * MainActivity creates one LiftViewModel and gives it to all three screens,
 * so they all share the same data. It also keeps its data when the phone is rotated.
 */
class LiftViewModel(application: Application) : AndroidViewModel(application) {

    // Open the database and get the queries
    private val dao = AppDatabase.getDatabase(application).liftDao()

    // Keeps the latest list of lifts ready for the screens to read (used on History).
    // It only listens to the database while a screen is open, and starts as an empty list
    val lifts: StateFlow<List<Lift>> = dao.getAllLifts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Same idea for personal bests (used on Stats and for the PB badge on History)
    val personalBests: StateFlow<List<PersonalBest>> = dao.getPersonalBests()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // viewModelScope.launch runs the database work in the background (a coroutine)
    // so the screen doesn't freeze. After any change, the lists above update by themselves.

    // Save a new lift (called from the Log screen)
    fun addLift(exercise: String, muscleGroup: String, weightKg: Double, sets: Int, reps: Int) {
        viewModelScope.launch {
            dao.insert(Lift(exercise = exercise, muscleGroup = muscleGroup, weightKg = weightKg, sets = sets, reps = reps))
        }
    }

    // Save changes to an existing lift (called from the Log screen when editing)
    fun updateLift(lift: Lift) {
        viewModelScope.launch { dao.update(lift) }
    }

    // Delete a lift (called from the History screen when a card is swiped away)
    fun deleteLift(lift: Lift) {
        viewModelScope.launch { dao.delete(lift) }
    }
}
