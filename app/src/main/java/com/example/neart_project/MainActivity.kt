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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.neart_project.screens.HistoryScreen
import com.example.neart_project.screens.LogScreen
import com.example.neart_project.screens.StatsScreen
import com.example.neart_project.ui.theme.Neart_ProjectTheme
import com.example.neart_project.viewmodel.LiftViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Neart_ProjectTheme {
                Neart_ProjectApp()
            }
        }
    }
}




@Composable
fun Neart_ProjectApp() {
    // One ViewModel shared by all three screens, so they all see the same data
    val viewModel: LiftViewModel = viewModel()
    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.HISTORY) }

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            item(
                icon = { Icon(painterResource(R.drawable.ic_history), contentDescription = "History") },
                label = { Text("History") },
                selected = currentDestination == AppDestinations.HISTORY,
                onClick = { currentDestination = AppDestinations.HISTORY }
            )
            item(
                icon = { Icon(painterResource(R.drawable.ic_dumbbell), contentDescription = "Log") },
                label = { Text("Log") },
                selected = currentDestination == AppDestinations.LOG,
                onClick = { currentDestination = AppDestinations.LOG }
            )
            item(
                icon = { Icon(painterResource(R.drawable.ic_stats), contentDescription = "Stats") },
                label = { Text("Stats") },
                selected = currentDestination == AppDestinations.STATS,
                onClick = { currentDestination = AppDestinations.STATS }
            )
        }
    )




    {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            // Show the screen for whichever tab is selected
            when (currentDestination) {
                AppDestinations.HISTORY -> HistoryScreen(
                    viewModel = viewModel,
                    onAddClick = { currentDestination = AppDestinations.LOG },
                    modifier = Modifier.padding(innerPadding)
                )
                AppDestinations.LOG -> LogScreen(
                    viewModel = viewModel,
                    onSaved = { currentDestination = AppDestinations.HISTORY },
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



enum class AppDestinations(
    val label: String,
    val icon: Int,
) {
    HISTORY("History", R.drawable.ic_history),
    LOG("Log", R.drawable.ic_dumbbell),
    STATS("Stats", R.drawable.ic_stats),
}