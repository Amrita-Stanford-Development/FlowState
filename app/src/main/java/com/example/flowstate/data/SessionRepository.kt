package com.example.flowstate.data

import androidx.compose.runtime.mutableStateListOf
import com.example.flowstate.model.Session
import java.text.SimpleDateFormat
import java.util.*

object SessionRepository {
    private val sessions = mutableStateListOf<Session>()

    fun addSession(session: Session) {
        sessions.add(session)
    }

    fun getAllSessions(): List<Session> = sessions.toList()

    fun getTodaySessions(): List<Session> {
        val today = Calendar.getInstance()
        today.set(Calendar.HOUR_OF_DAY, 0)
        today.set(Calendar.MINUTE, 0)
        today.set(Calendar.SECOND, 0)
        today.set(Calendar.MILLISECOND, 0)
        val todayStart = today.timeInMillis

        return sessions.filter { it.startTime >= todayStart }
    }

    fun getStreak(): Int {
        if (sessions.isEmpty()) return 0

        val cal = Calendar.getInstance()
        var streak = 0
        var currentDate = cal.clone() as Calendar

        while (true) {
            val dayStart = currentDate.clone() as Calendar
            dayStart.set(Calendar.HOUR_OF_DAY, 0)
            dayStart.set(Calendar.MINUTE, 0)
            dayStart.set(Calendar.SECOND, 0)
            dayStart.set(Calendar.MILLISECOND, 0)

            val dayEnd = currentDate.clone() as Calendar
            dayEnd.set(Calendar.HOUR_OF_DAY, 23)
            dayEnd.set(Calendar.MINUTE, 59)
            dayEnd.set(Calendar.SECOND, 59)
            dayEnd.set(Calendar.MILLISECOND, 999)

            val hasSessions = sessions.any {
                it.startTime >= dayStart.timeInMillis && it.startTime <= dayEnd.timeInMillis
            }

            if (hasSessions) {
                streak++
                currentDate.add(Calendar.DAY_OF_MONTH, -1)
            } else {
                break
            }
        }

        return streak
    }

    fun getTotalSessions(): Int = sessions.size

    fun isSameDay(timestamp1: Long, timestamp2: Long): Boolean {
        val cal1 = Calendar.getInstance().apply { timeInMillis = timestamp1 }
        val cal2 = Calendar.getInstance().apply { timeInMillis = timestamp2 }

        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
               cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }
}
