package com.example.flowstate.model

data class Session(
    val id: Long = System.currentTimeMillis(),
    val mood: Mood,
    val note: String = "",
    val workDuration: Int = 25,
    val breakDuration: Int = 5,
    val startTime: Long = System.currentTimeMillis(),
    val endTime: Long? = null,
    val rating: Int? = null
)
