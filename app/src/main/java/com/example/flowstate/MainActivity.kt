package com.example.flowstate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import com.example.flowstate.data.SessionRepository
import com.example.flowstate.model.Mood
import com.example.flowstate.model.Screen
import com.example.flowstate.model.Session
import com.example.flowstate.ui.screens.*
import com.example.flowstate.ui.theme.FlowStateTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            var isDarkTheme by remember { mutableStateOf(false) }

            FlowStateTheme(darkTheme = isDarkTheme) {
                val backgroundColor = MaterialTheme.colorScheme.background

                LaunchedEffect(backgroundColor) {
                    window.statusBarColor = backgroundColor.toArgb()
                    window.navigationBarColor = backgroundColor.toArgb()

                    WindowCompat.getInsetsController(window, window.decorView).apply {
                        isAppearanceLightStatusBars = !isDarkTheme
                        isAppearanceLightNavigationBars = !isDarkTheme
                    }
                }

                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .safeDrawingPadding()
                ) {
                    FlowStateApp(
                        isDarkTheme = isDarkTheme,
                        onThemeToggle = { isDarkTheme = !isDarkTheme }
                    )
                }
            }
        }
    }
}

@Composable
fun FlowStateApp(
    isDarkTheme: Boolean = false,
    onThemeToggle: () -> Unit = {}
) {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Intro) }
    var currentSession by remember { mutableStateOf<Session?>(null) }

    when (val screen = currentScreen) {
        is Screen.Intro -> {
            IntroScreen(
                onIntroComplete = {
                    currentScreen = Screen.Dashboard
                }
            )
        }
        is Screen.MoodSelector -> {
            MoodSelectorScreen(
                onMoodSelected = { mood, note, workDuration, breakDuration ->
                    currentScreen = Screen.PomodoroTimer(mood, note, workDuration, breakDuration)
                },
                onBackPressed = {
                    currentScreen = Screen.Dashboard
                }
            )
        }
        is Screen.PomodoroTimer -> {
            PomodoroTimerScreen(
                mood = screen.mood,
                note = screen.note,
                workDuration = screen.workDuration,
                breakDuration = screen.breakDuration,
                onSessionComplete = { rating ->
                    val session = Session(
                        mood = screen.mood,
                        note = screen.note,
                        workDuration = screen.workDuration,
                        breakDuration = screen.breakDuration,
                        endTime = System.currentTimeMillis(),
                        rating = null
                    )
                    currentSession = session
                    currentScreen = Screen.SessionRating(session)
                },
                onBackPressed = {
                    currentScreen = Screen.Dashboard
                }
            )
        }
        is Screen.SessionRating -> {
            SessionRatingScreen(
                mood = screen.session.mood,
                onRatingSelected = { rating ->
                    currentSession?.let { session ->
                        val updatedSession = session.copy(rating = rating)
                        SessionRepository.addSession(updatedSession)
                    }
                    currentScreen = Screen.Dashboard
                }
            )
        }
        is Screen.Dashboard -> {
            DashboardScreen(
                onStartNewSession = {
                    currentScreen = Screen.MoodSelector
                },
                onViewStatistics = {
                    currentScreen = Screen.Statistics
                },
                isDarkTheme = isDarkTheme,
                onThemeToggle = onThemeToggle
            )
        }
        is Screen.Statistics -> {
            StatisticsScreen(
                onBackClick = {
                    currentScreen = Screen.Dashboard
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MoodSelectorPreview() {
    FlowStateTheme {
        MoodSelectorScreen(
            onMoodSelected = { _, _, _, _ -> },
            onBackPressed = {}
        )
    }
}