package com.example.neart_project

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.neart_project.data.Lift
import com.example.neart_project.screens.HistoryScreen
import com.example.neart_project.screens.LogScreen
import com.example.neart_project.screens.StatsScreen
import com.example.neart_project.ui.theme.Neart_ProjectTheme
import com.example.neart_project.viewmodel.LiftViewModel

/**
 * MainActivity - where the app starts.
 * There's only one Activity; it shows the Compose UI and switches between the three screens.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Let the app draw right up to the edges of the screen (behind the status bar)
        enableEdgeToEdge()
        // Everything inside setContent is the app's UI
        setContent {
            // Apply the app's colours and fonts (from ui/theme)
            Neart_ProjectTheme {
                Neart_ProjectApp()
            }
        }
    }
}


/**
 * Neart_ProjectApp - holds the navigation bar and decides which screen to show.
 *
 * currentDestination stores the selected tab. When it changes, Compose redraws
 * and shows the matching screen.
 */
@Composable
fun Neart_ProjectApp() {
    // One ViewModel shared by all three screens, so they all see the same data
    val viewModel: LiftViewModel = viewModel()

    // The selected tab. rememberSaveable keeps it when the phone is rotated. Starts on History
    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.HISTORY) }

    // The lift being edited, or null if adding a new one.
    // Set when a card is tapped on History, then given to the Log screen
    var editingLift by remember { mutableStateOf<Lift?>(null) }

    // Navigation bar: shows at the bottom on phones, and on the side on bigger screens
    NavigationSuiteScaffold(
        navigationSuiteItems = {
            // One item per tab. Tapping one changes the selected tab
            item(
                // Accessibility: TalkBack (screen reader) reads "History" for this icon,
                // so users who can't see the screen know this tab opens their lift history
                icon = { Icon(painterResource(R.drawable.ic_history), contentDescription = stringResource(R.string.history)) },
                label = { Text(stringResource(R.string.history)) },
                selected = currentDestination == AppDestinations.HISTORY,
                onClick = { currentDestination = AppDestinations.HISTORY }
            )
            item(
                // Accessibility: TalkBack reads "Log" for the dumbbell icon, so users know
                // this tab opens the form for logging a new lift
                icon = { Icon(painterResource(R.drawable.ic_dumbbell), contentDescription = stringResource(R.string.log)) },
                label = { Text(stringResource(R.string.log)) },
                selected = currentDestination == AppDestinations.LOG,
                onClick = {
                    // Opening Log from the nav bar is always for a new lift, so clear any edit
                    editingLift = null
                    currentDestination = AppDestinations.LOG
                }
            )
            item(
                // Accessibility: TalkBack reads "Stats" for this icon, so users know
                // this tab shows their personal bests and the quote of the day
                icon = { Icon(painterResource(R.drawable.ic_stats), contentDescription = stringResource(R.string.stats)) },
                label = { Text(stringResource(R.string.stats)) },
                selected = currentDestination == AppDestinations.STATS,
                onClick = { currentDestination = AppDestinations.STATS }
            )
        }
    )
    // The screen content
    {
        // innerPadding stops the screens being hidden behind the system bars
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            // Show the screen for the selected tab.
            // The screens don't switch tabs themselves - they call these functions
            // (onAddClick, onEdit, onSaved) and this code changes the tab
            when (currentDestination) {
                AppDestinations.HISTORY -> HistoryScreen(
                    viewModel = viewModel,
                    // "+" tapped: open Log with an empty form
                    onAddClick = {
                        editingLift = null
                        currentDestination = AppDestinations.LOG
                    },
                    // Card tapped: open Log with that lift filled in
                    onEdit = { lift ->
                        editingLift = lift
                        currentDestination = AppDestinations.LOG
                    },
                    modifier = Modifier.padding(innerPadding)
                )
                AppDestinations.LOG -> LogScreen(
                    viewModel = viewModel,
                    liftToEdit = editingLift,   // null = new lift
                    // After saving, go back to History
                    onSaved = {
                        editingLift = null
                        currentDestination = AppDestinations.HISTORY
                    },
                    modifier = Modifier.padding(innerPadding)
                )
                AppDestinations.STATS -> StatsScreen(
                    viewModel = viewModel,
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }
}


// The three tabs, each with a name and an icon (icons are in res/drawable)
enum class AppDestinations(
    val label: String,
    val icon: Int,
) {
    HISTORY("History", R.drawable.ic_history),
    LOG("Log", R.drawable.ic_dumbbell),
    STATS("Stats", R.drawable.ic_stats),
}
