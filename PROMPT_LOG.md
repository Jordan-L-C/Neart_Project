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

---

### Entry 5: Code Documentation Comments
- **User Prompt**: *"Please add clear comments to everything outlining important features and how it interacts with other files."*
- **Summary / Resolution**:
  - Added explanatory comments across all main source files without changing any code behaviour:
    - **`Lift.kt`**: Entity/table mapping, auto-generated primary key, and how `date` is used for sorting and kept when a lift is edited.
    - **`LiftDao.kt`**: Why reads return `Flow` (automatic UI updates) and writes are `suspend` (no database work on the main thread), how the personal bests SQL query works, and which ViewModel function calls each operation.
    - **`AppDatabase.kt`**: Singleton pattern, `@Volatile`/`synchronized`, use of `applicationContext`, and what the schema `version` means for future migrations.
    - **`LiftViewModel.kt`**: Its role in MVVM between the screens and the DAO, how `stateIn`/`WhileSubscribed(5000)` turns Room Flows into `StateFlow`, and how `viewModelScope` coroutines handle writes.
    - **`MainActivity.kt`**: State-based navigation, the shared ViewModel, state hoisting through callbacks (`onAddClick`, `onEdit`, `onSaved`), `editingLift` for edit mode, and the adaptive `NavigationSuiteScaffold`.
    - **`History.kt`**: Live list collection, PB badge logic, why `LazyColumn` items are keyed by `id`, and how the `SwipeToDismissBox` + `LaunchedEffect` delete works.
    - **`Log.kt`**: Add vs. edit modes, `rememberSaveable(liftToEdit)` form state, weight validation, `copy()` keeping the original id for updates, and the reusable stateless `DropdownField`.
    - **`Stats.kt`**: Where personal bests come from, and the placeholder quote card that will later use the quote API.

---

### Entry 6: Quote of the Day API (Retrofit)
- **User Prompt**: *"Please walk me through implementing a quote API on the Stats screen, so that a motivational quote of the day is displayed."*
- **Summary / Resolution**:
  - Chose the free ZenQuotes API (`https://zenquotes.io/api/today`), which needs no API key and returns one quote per day as JSON.
  - **Dependencies**: Added Retrofit 3.0.0 and its Gson converter to `libs.versions.toml` and `app/build.gradle.kts`.
  - **`AndroidManifest.xml`**: Added the `INTERNET` permission so the app can make network requests.
  - **`data/QuoteApi.kt`**:
    - `Quote` data class (`q` = quote text, `a` = author) matching the API's JSON field names so Gson can convert the response automatically.
    - `QuoteApi` Retrofit interface with a `suspend` `@GET("api/today")` function, plus a single shared `service` instance created with `by lazy`.
  - **`LiftViewModel.kt`**: Added a `quote` state (`mutableStateOf<Quote?>`) that is fetched once in `init` using `viewModelScope.launch`, wrapped in `try/catch` so having no internet can't crash the app.
  - **`Stats.kt`**: The quote card now shows the API quote and its author, falling back to the original hardcoded quote while loading or when offline.

---

### Entry 7: Accessibility Features Document
- **User Prompt**: *"Please create a text document similar to the prompt log outlining the accessibility features I have implemented so it is easier to demo."*
- **Summary / Resolution**:
  - Created `ACCESSIBILITY.md` in the project root, listing each implemented accessibility feature with a short explanation and the matching code example:
    1. Labelled navigation icons (`contentDescription` for TalkBack)
    2. Labelled form fields
    3. Errors not shown by colour alone (red field plus a written message)
    4. Readable colour contrast from Material Theme Builder (`primary`/`onPrimary` pairs)
    5. Text that scales with the user's font size (`sp` typography and a scrollable form)
    6. Minimal typing (dropdowns, chips and a number keyboard)
    7. Clear empty states on the History and Stats screens

---

### Entry 8: Shake to Start Rest Timer (Accelerometer)
- **User Prompt**: *"Walk me through how to implement a shake to start rest timer, and explain how everything works and ties in with the rest of the app."*
- **Summary / Resolution**:
  - Implemented the second technical feature from the design: shaking the phone on any screen starts a rest timer.
  - **`sensors/ShakeDetector.kt`**: A `SensorEventListener` that combines the accelerometer's x, y and z readings into one g-force value. A reading above `SHAKE_THRESHOLD` (1.8) counts as a shake, with a 1 second cooldown so one shake doesn't trigger multiple times.
  - **`screens/RestTimerDialog.kt`**: An `AlertDialog` that counts down from 60 seconds using `LaunchedEffect` and `delay(1000)`, then shows a "Rest over" message. The countdown uses large text so it's easy to read from a distance.
  - **`MainActivity.kt`**: Registers the shake detector with the `SensorManager` using `LifecycleResumeEffect`, so it only listens while the app is on screen and stops in the background to save battery. Shows the rest timer on top of whichever screen is open.
  - **`strings.xml` / `values-ga/strings.xml`**: Added English and Irish text for the timer.
  - Can be tested on the emulator using Virtual sensors (Device Pose → Move), as no physical device was available.
