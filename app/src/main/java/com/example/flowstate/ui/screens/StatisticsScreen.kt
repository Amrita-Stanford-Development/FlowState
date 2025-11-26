package com.example.flowstate.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flowstate.data.SessionRepository
import com.example.flowstate.model.Mood
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    onBackClick: () -> Unit
) {
    val sessions = remember { SessionRepository.getAllSessions() }
    val totalMinutes = remember { sessions.sumOf { it.workDuration } }
    val averageRating = remember {
        val ratedSessions = sessions.filter { it.rating != null && it.rating > 0 }
        if (ratedSessions.isNotEmpty()) {
            ratedSessions.mapNotNull { it.rating }.average()
        } else 0.0
    }

    val moodBreakdown = remember {
        sessions.groupBy { it.mood }.mapValues { it.value.size }
    }

    val bestDay = remember {
        sessions.groupBy { session ->
            val cal = Calendar.getInstance()
            cal.timeInMillis = session.startTime
            SimpleDateFormat("MMM dd", Locale.getDefault()).format(cal.time)
        }.maxByOrNull { it.value.size }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Statistics & Insights") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
                .padding(padding)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "Overview",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF333333)
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        StatRow("Total Sessions", "${sessions.size}", "🎯")
                        StatRow("Total Focus Time", "${totalMinutes} min", "⏱️")
                        StatRow("Average Rating", String.format("%.1f/5", averageRating), "⭐")
                        StatRow("Current Streak", "${SessionRepository.getStreak()} days", "🔥")
                    }
                }
            }

            if (moodBreakdown.isNotEmpty()) {
                item {
                    Text(
                        text = "Mood Distribution",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF333333),
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            moodBreakdown.entries.sortedByDescending { it.value }.forEach { (mood, count) ->
                                MoodStatRow(mood, count, sessions.size)
                            }
                        }
                    }
                }
            }

            if (bestDay != null) {
                item {
                    Text(
                        text = "Personal Best",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF333333),
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFD700).copy(alpha = 0.1f)),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(text = "🏆", fontSize = 48.sp)
                            Text(
                                text = "Best Day: ${bestDay.key}",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF333333)
                            )
                            Text(
                                text = "${bestDay.value.size} sessions completed",
                                fontSize = 14.sp,
                                color = Color(0xFF666666)
                            )
                        }
                    }
                }
            }

            item {
                Text(
                    text = "Productivity Tips",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF333333),
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF667eea).copy(alpha = 0.1f)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        TipItem("💡", "Take regular breaks to maintain focus")
                        TipItem("🎧", "Use background music to enhance concentration")
                        TipItem("📝", "Write notes to track your progress")
                        TipItem("🌟", "Rate your sessions to identify patterns")
                    }
                }
            }
        }
    }
}

@Composable
fun StatRow(label: String, value: String, emoji: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = emoji, fontSize = 20.sp)
            Text(
                text = label,
                fontSize = 16.sp,
                color = Color(0xFF666666)
            )
        }
        Text(
            text = value,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF333333)
        )
    }
}

@Composable
fun MoodStatRow(mood: Mood, count: Int, total: Int) {
    val percentage = (count.toFloat() / total * 100).toInt()

    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = mood.emoji, fontSize = 20.sp)
                Text(
                    text = mood.label,
                    fontSize = 16.sp,
                    color = Color(0xFF666666)
                )
            }
            Text(
                text = "$count sessions ($percentage%)",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = mood.color
            )
        }

        LinearProgressIndicator(
            progress = percentage / 100f,
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp),
            color = mood.color,
            trackColor = mood.color.copy(alpha = 0.2f)
        )
    }
}

@Composable
fun TipItem(emoji: String, text: String) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = emoji, fontSize = 20.sp)
        Text(
            text = text,
            fontSize = 14.sp,
            color = Color(0xFF666666)
        )
    }
}
