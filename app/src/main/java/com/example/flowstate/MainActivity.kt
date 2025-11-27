package com.example.flowstate

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import com.example.flowstate.data.SessionRepository
import com.example.flowstate.model.Screen
import com.example.flowstate.model.Session
import com.example.flowstate.ui.screens.*
import com.example.flowstate.ui.theme.FlowStateTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize SessionRepository to load saved sessions
        SessionRepository.initialize(applicationContext)

        // Keep bars transparent so we can see the Surface background behind them
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(
                Color.TRANSPARENT,
                Color.TRANSPARENT
            ),
            navigationBarStyle = SystemBarStyle.auto(
                Color.TRANSPARENT,
                Color.TRANSPARENT
            )
        )

        setContent {
            var isDarkTheme by remember { mutableStateOf(false) }

            // Manage Status Bar Icon Colors (White icons for Dark Mode, Black for Light)
            val view = LocalView.current
            if (!view.isInEditMode) {
                SideEffect {
                    val window = (view.context as Activity).window
                    val insetsController = WindowCompat.getInsetsController(window, view)

                    // !isDarkTheme means:
                    // If Dark Mode is ON -> isAppearanceLightStatusBars = FALSE -> White Icons
                    // If Dark Mode is OFF -> isAppearanceLightStatusBars = TRUE -> Black Icons
                    insetsController.isAppearanceLightStatusBars = !isDarkTheme
                    insetsController.isAppearanceLightNavigationBars = !isDarkTheme
                }
            }

            FlowStateTheme(darkTheme = isDarkTheme) {
                // CHANGE IS HERE:
                // 1. Surface fills the WHOLE screen (including behind status bar)
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background // Paints the area Black in Dark Mode
                ) {
                    // 2. We add a container inside that respects the padding
                    // This ensures your content doesn't go under the clock/battery
                    Box(modifier = Modifier.safeDrawingPadding()) {
                        FlowStateApp(
                            isDarkTheme = isDarkTheme,
                            onThemeToggle = { isDarkTheme = !isDarkTheme }
                        )
                    }
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
                    // If mood is stressed or tired, start with breathing exercise
                    currentScreen = if (mood == com.example.flowstate.model.Mood.STRESSED ||
                                       mood == com.example.flowstate.model.Mood.TIRED) {
                        Screen.BreathingExercise(mood, note, workDuration, breakDuration)
                    } else {
                        Screen.PomodoroTimer(mood, note, workDuration, breakDuration)
                    }
                },
                onBackPressed = {
                    currentScreen = Screen.Dashboard
                }
            )
        }
        is Screen.BreathingExercise -> {
            BreathingExerciseScreen(
                onComplete = {
                    currentScreen = Screen.PomodoroTimer(
                        screen.mood,
                        screen.note,
                        screen.workDuration,
                        screen.breakDuration
                    )
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
                    val session = screen.session.copy(rating = rating)
                    SessionRepository.addSession(session)
                    currentSession = null
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