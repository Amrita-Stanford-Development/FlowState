package com.example.flowstate.model

data class TimerPreset(
    val name: String,
    val workDuration: Int,
    val breakDuration: Int,
    val emoji: String
) {
    companion object {
        val DEFAULT_PRESETS = listOf(
            TimerPreset("Debug Mode", 0, 0, "🐛"),
            TimerPreset("Classic Pomodoro", 25, 5, "🍅"),
            TimerPreset("Short Sprint", 15, 3, "⚡"),
            TimerPreset("Deep Focus", 50, 10, "🎯"),
            TimerPreset("Study Session", 45, 15, "📚"),
            TimerPreset("Quick Break", 10, 2, "☕")
        )
    }
}
