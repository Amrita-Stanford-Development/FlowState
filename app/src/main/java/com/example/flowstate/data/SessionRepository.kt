package com.example.flowstate.data

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.mutableStateListOf
import com.example.flowstate.model.Mood
import com.example.flowstate.model.Session
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

object SessionRepository {
    private val sessions = mutableStateListOf<Session>()
    private var sharedPreferences: SharedPreferences? = null
    private const val PREFS_NAME = "flowstate_sessions"
    private const val KEY_SESSIONS = "sessions"

    fun initialize(context: Context) {
        if (sharedPreferences == null) {
            sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            loadSessions()
        }
    }

    private fun loadSessions() {
        try {
            val sessionsJson = sharedPreferences?.getString(KEY_SESSIONS, null)
            if (sessionsJson != null) {
                val jsonArray = JSONArray(sessionsJson)
                sessions.clear()
                for (i in 0 until jsonArray.length()) {
                    val sessionObj = jsonArray.getJSONObject(i)
                    val session = Session(
                        id = sessionObj.getLong("id"),
                        mood = Mood.valueOf(sessionObj.getString("mood")),
                        note = sessionObj.optString("note", ""),
                        workDuration = sessionObj.getInt("workDuration"),
                        breakDuration = sessionObj.getInt("breakDuration"),
                        startTime = sessionObj.getLong("startTime"),
                        endTime = if (sessionObj.has("endTime")) sessionObj.getLong("endTime") else null,
                        rating = if (sessionObj.has("rating")) sessionObj.getInt("rating") else null
                    )
                    sessions.add(session)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun saveSessions() {
        try {
            val jsonArray = JSONArray()
            sessions.forEach { session ->
                val sessionObj = JSONObject().apply {
                    put("id", session.id)
                    put("mood", session.mood.name)
                    put("note", session.note)
                    put("workDuration", session.workDuration)
                    put("breakDuration", session.breakDuration)
                    put("startTime", session.startTime)
                    if (session.endTime != null) put("endTime", session.endTime)
                    if (session.rating != null) put("rating", session.rating)
                }
                jsonArray.put(sessionObj)
            }
            sharedPreferences?.edit()?.putString(KEY_SESSIONS, jsonArray.toString())?.apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun addSession(session: Session) {
        sessions.add(session)
        saveSessions()
    }

    fun updateSession(session: Session) {
        val index = sessions.indexOfFirst { it.id == session.id }
        if (index != -1) {
            sessions[index] = session
            saveSessions()
        }
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
