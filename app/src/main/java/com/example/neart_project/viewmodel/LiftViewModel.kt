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

class LiftViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = AppDatabase.getDatabase(application).liftDao()

    // All lifts, newest first - updates automatically when the table changes
    val lifts: StateFlow<List<Lift>> = dao.getAllLifts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Heaviest weight per exercise
    val personalBests: StateFlow<List<PersonalBest>> = dao.getPersonalBests()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Database writes run in a coroutine so the screen doesn't freeze
    fun addLift(exercise: String, muscleGroup: String, weightKg: Double, sets: Int, reps: Int) {
        viewModelScope.launch {
            dao.insert(Lift(exercise = exercise, muscleGroup = muscleGroup, weightKg = weightKg, sets = sets, reps = reps))
        }
    }

    fun updateLift(lift: Lift) {
        viewModelScope.launch { dao.update(lift) }
    }

    fun deleteLift(lift: Lift) {
        viewModelScope.launch { dao.delete(lift) }
    }
}