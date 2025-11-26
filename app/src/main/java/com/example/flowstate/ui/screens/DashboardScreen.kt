package com.example.flowstate.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flowstate.data.SessionRepository
import com.example.flowstate.model.DailyGoal
import com.example.flowstate.model.Session
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DashboardScreen(
    onStartNewSession: () -> Unit,
    onViewStatistics: () -> Unit = {},
    isDarkTheme: Boolean = false,
    onThemeToggle: () -> Unit = {}
) {
    val todaySessions = remember { SessionRepository.getTodaySessions() }
    val allSessions = remember { SessionRepository.getAllSessions() }
    val streak = remember { SessionRepository.getStreak() }
    var selectedSession by remember { mutableStateOf<Session?>(null) }
    val dailyGoal = remember { DailyGoal() }
    val todayMinutes = remember { todaySessions.sumOf { it.workDuration } }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(bottom = 80.dp),
            contentPadding = PaddingValues(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Dashboard",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    IconButton(
                        onClick = onThemeToggle,
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Text(
                            text = if (isDarkTheme) "☀️" else "🌙",
                            fontSize = 24.sp
                        )
                    }
                }
            }

            item {
                DailyGoalCard(
                    goalSessions = dailyGoal.targetSessions,
                    currentSessions = todaySessions.size,
                    goalMinutes = dailyGoal.targetMinutes,
                    currentMinutes = todayMinutes
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onViewStatistics() },
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF667eea)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "📊",
                                fontSize = 20.sp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "View Statistics",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            item {
                StatsCard(
                    todayCount = todaySessions.size,
                    totalCount = allSessions.size,
                    streak = streak
                )
            }

            item {
                Text(
                    text = "Today's Sessions",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            if (todaySessions.isEmpty()) {
                item {
                    EmptySessionsCard()
                }
            } else {
                item {
                    SessionDotsRow(sessions = todaySessions)
                }

                items(todaySessions.reversed()) { session ->
                    SessionCard(
                        session = session,
                        onClick = { selectedSession = session }
                    )
                }
            }

            if (allSessions.size > todaySessions.size) {
                item {
                    Text(
                        text = "Previous Sessions",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }

                items(allSessions.reversed().filter { !todaySessions.contains(it) }.take(10)) { session ->
                    SessionCard(
                        session = session,
                        onClick = { selectedSession = session }
                    )
                }
            }
        }

        FloatingActionButton(
            onClick = onStartNewSession,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp),
            containerColor = Color(0xFF667eea)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Start New Session",
                tint = Color.White
            )
        }
    }

    if (selectedSession != null) {
        SessionDetailDialog(
            session = selectedSession!!,
            onDismiss = { selectedSession = null }
        )
    }
}

@Composable
fun StatsCard(
    todayCount: Int,
    totalCount: Int,
    streak: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatItem(
                value = todayCount.toString(),
                label = "Today",
                emoji = "📅"
            )
            Divider(
                modifier = Modifier
                    .height(60.dp)
                    .width(1.dp),
                color = MaterialTheme.colorScheme.outline
            )
            StatItem(
                value = totalCount.toString(),
                label = "Total",
                emoji = "🎯"
            )
            Divider(
                modifier = Modifier
                    .height(60.dp)
                    .width(1.dp),
                color = MaterialTheme.colorScheme.outline
            )
            StatItem(
                value = streak.toString(),
                label = "Streak",
                emoji = "🔥"
            )
        }
    }
}

@Composable
fun StatItem(
    value: String,
    label: String,
    emoji: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = emoji,
            fontSize = 24.sp
        )
        Text(
            text = value,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = label,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun SessionDotsRow(sessions: List<Session>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Completed:",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Medium
        )
        sessions.forEach { session ->
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(session.mood.color)
            )
        }
    }
}

@Composable
fun SessionCard(
    session: Session,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .background(
                        color = session.mood.color.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = session.mood.emoji,
                    fontSize = 32.sp
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = session.mood.label,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (session.rating != null && session.rating > 0) {
                        Text(
                            text = getRatingEmoji(session.rating),
                            fontSize = 16.sp
                        )
                    }
                }

                Text(
                    text = formatTimestamp(session.startTime),
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (session.note.isNotEmpty()) {
                    Text(
                        text = session.note,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant, // Fixed hardcoded grey
                        maxLines = 1,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            Text(
                text = "${session.workDuration}m",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = session.mood.color
            )
        }
    }
}

@Composable
fun EmptySessionsCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "🌱",
                fontSize = 48.sp
            )
            Text(
                text = "No sessions yet today",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Start your first session!",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant, // Fixed hardcoded grey
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun SessionDetailDialog(
    session: Session,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(text = session.mood.emoji, fontSize = 24.sp)
                Text(
                    text = session.mood.label,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                DetailRow(label = "Time", value = formatTimestamp(session.startTime))
                DetailRow(label = "Duration", value = "${session.workDuration} minutes")
                if (session.rating != null && session.rating > 0) {
                    DetailRow(
                        label = "Rating",
                        value = "${getRatingEmoji(session.rating)} ${session.rating}/5"
                    )
                }
                if (session.note.isNotEmpty()) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "Note:",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = session.note,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        },
        containerColor = MaterialTheme.colorScheme.surface // FIXED: Was Color.White
    )
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "$label:",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

fun formatTimestamp(timestamp: Long): String {
    val sdf = SimpleDateFormat("MMM dd, hh:mm a", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

fun getRatingEmoji(rating: Int): String {
    return when (rating) {
        1 -> "😵"
        2 -> "😕"
        3 -> "😐"
        4 -> "🙂"
        5 -> "😄"
        else -> ""
    }
}

@Composable
fun DailyGoalCard(
    goalSessions: Int,
    currentSessions: Int,
    goalMinutes: Int,
    currentMinutes: Int
) {
    val sessionProgress = (currentSessions.toFloat() / goalSessions).coerceAtMost(1f)
    val minuteProgress = (currentMinutes.toFloat() / goalMinutes).coerceAtMost(1f)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            // FIXED: Now uses surface color for dark mode compatibility
            containerColor = if (sessionProgress >= 1f && minuteProgress >= 1f)
                Color(0xFF4CAF50).copy(alpha = 0.1f)
            else
                MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Daily Goal",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (sessionProgress >= 1f && minuteProgress >= 1f) {
                    Text(
                        text = "🎉 Completed!",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4CAF50)
                    )
                }
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Sessions",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "$currentSessions / $goalSessions",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    LinearProgressIndicator(
                        progress = sessionProgress,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp),
                        color = Color(0xFF667eea),
                        trackColor = Color(0xFF667eea).copy(alpha = 0.2f)
                    )
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Focus Time",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "$currentMinutes / $goalMinutes min",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    LinearProgressIndicator(
                        progress = minuteProgress,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp),
                        color = Color(0xFF4CAF50),
                        trackColor = Color(0xFF4CAF50).copy(alpha = 0.2f)
                    )
                }
            }
        }
    }
}