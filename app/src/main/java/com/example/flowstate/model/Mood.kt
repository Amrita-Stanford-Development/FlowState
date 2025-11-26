package com.example.flowstate.model

import androidx.compose.ui.graphics.Color

enum class Mood(
    val emoji: String,
    val label: String,
    val color: Color
) {
    ENERGIZED("⚡", "Energized", Color(0xFFFFB74D)),
    CALM("🌊", "Calm", Color(0xFF64B5F6)),
    STRESSED("😰", "Stressed", Color(0xFFEF5350)),
    TIRED("😴", "Tired", Color(0xFF9575CD))
}
