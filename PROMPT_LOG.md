# Prompt Log

This document logs user prompts, AI assistant responses, architectural decisions, code modifications, and feature implementations throughout the project lifecycle.

---

## Session Overview

- **Project Name**: Neart_Project (Android Workout Tracker App)
- **Tech Stack**: Kotlin, Jetpack Compose, Material 3, Room Database, Retrofit API, Coroutines & Flow, MVVM Architecture
- **Repository Branch**: `master`

---

## Log Entries

### Entry 1: Create Prompt Log
- **User Prompt**: *"Please create a prompt log to record the prompts used for this project."*
- **Summary / Resolution**:
  - Initialized `PROMPT_LOG.md` in the project root directory to maintain an organized record of prompts, architectural decisions, code edits, and feature milestones.

---

### Entry 2: Room Database Architecture & Setup
- **User Prompt**: *"I need help implementing a database for saving and deleting lifts, how do I go about this and explain to me how and why everything works"*
- **Summary / Resolution**:
  - Provided a complete architectural walkthrough for setting up local persistence using Android Room and MVVM:
    1. **Data Entity (`Lift`)**: Defined table structure `@Entity(tableName = "lifts")` with auto-generated `@PrimaryKey`.
    2. **DAO (`LiftDao`)**: Implemented database operations (`@Insert`, `@Delete`, and `@Query` returning reactive `Flow<List<Lift>>`).
    3. **Database (`AppDatabase`)**: Built singleton Room database instance with thread-safe double-checked locking (`@Volatile` + `synchronized`).
    4. **ViewModel (`LiftViewModel`)**: Exposed reactive data via `StateFlow` and dispatched coroutine operations (`viewModelScope.launch`).
    5. **UI Layer (`Jetpack Compose`)**: Connected screens to collect state safely with `collectAsStateWithLifecycle()`.

---

### Entry 3: Swipe-to-Delete Feature
- **User Prompt**: *"I need a composable or a feature to make the cards on the log screen removable by swiping, what is available to me?"*
- **Summary / Resolution**:
  - Evaluated Material 3 `SwipeToDismissBox` component for Jetpack Compose.
  - Implemented `SwipeableLiftCard` in `HistoryScreen` with right-to-left (`EndToStart`) gesture detection revealing an error-tinted background card with a "Delete" label.
  - Connected the dismiss action to `viewModel.deleteLift(lift)` to remove the lift record from Room SQLite database.

---

### Entry 4: Fix & Complete Lift Editing Feature
- **User Prompt**: *"I have implemented the feature to edit lifts, but it is not working correctly, I am stuck, could you correct this please and explain what was wrong?"*
- **Summary / Resolution**:
  - Analyzed state flow and UI bindings across `MainActivity.kt`, `HistoryScreen`, and `LogScreen`:
    - **HistoryScreen**: Added `onEdit: (Lift) -> Unit` callback parameter and attached click handlers to `SwipeableLiftCard`.
    - **MainActivity**: Managed `editingLift` state and passed the selected lift to `LogScreen`.
    - **LogScreen Form State**: Updated form fields (`exercise`, `muscleGroup`, `weight`, `sets`, `reps`) using `rememberSaveable(liftToEdit)` so existing data pre-populates when editing.
    - **Database Update Logic**: Configured save button to call `viewModel.updateLift(...)` when `liftToEdit != null`, or `viewModel.addLift(...)` when creating a new record.
