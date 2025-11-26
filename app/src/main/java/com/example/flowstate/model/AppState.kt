package com.example.flowstate.model

sealed class Screen {
    object Intro : Screen()
    object MoodSelector : Screen()
    data class PomodoroTimer(val mood: Mood, val note: String, val workDuration: Int = 25, val breakDuration: Int = 5) : Screen()
    data class SessionRating(val session: Session) : Screen()
    object Dashboard : Screen()
    object Statistics : Screen()
}
