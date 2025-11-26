package com.example.flowstate.ui.screens

import android.content.ContentResolver
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flowstate.model.Mood
import com.example.flowstate.util.MusicPlayer
import com.example.flowstate.util.MusicTrack
import kotlinx.coroutines.delay
import java.io.File

enum class FocusSound(val label: String, val emoji: String) {
    SILENCE("Silence", "🔇"),
    RAIN("Rain", "🌧️"),
    CAFE("Cafe", "☕")
}

enum class TimerState {
    IDLE, RUNNING, PAUSED, BREAK
}

@Composable
fun PomodoroTimerScreen(
    mood: Mood,
    note: String,
    workDuration: Int = 25,
    breakDuration: Int = 5,
    onSessionComplete: (Int) -> Unit,
    onBackPressed: () -> Unit = {}
) {
    val context = LocalContext.current
    val musicPlayer = remember { MusicPlayer(context) }
    val musicTracks = remember { MusicPlayer.getMusicTracks(context) }

    var timerState by remember { mutableStateOf(TimerState.IDLE) }
    var currentWorkDuration by remember { mutableStateOf(workDuration) }
    var currentBreakDuration by remember { mutableStateOf(breakDuration) }

    // Debug mode: if workDuration is 0, use 5 seconds instead of minutes
    val workSeconds = if (workDuration == 0) 5 else workDuration * 60
    val breakSeconds = if (breakDuration == 0) 5 else breakDuration * 60

    var timeLeft by remember { mutableStateOf(workSeconds) }
    var isWorkSession by remember { mutableStateOf(true) }
    var selectedSound by remember { mutableStateOf(FocusSound.SILENCE) }
    var isSoundEnabled by remember { mutableStateOf(false) }
    var showSettings by remember { mutableStateOf(false) }
    var showMusicDialog by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        onDispose {
            musicPlayer.release()
        }
    }

    val backgroundColor = when (timerState) {
        TimerState.BREAK -> Color(0xFFE8F5E9)
        else -> mood.color.copy(alpha = 0.1f)
    }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    LaunchedEffect(timerState, timeLeft) {
        if (timerState == TimerState.RUNNING && timeLeft > 0) {
            delay(1000)
            timeLeft--
        } else if (timerState == TimerState.RUNNING && timeLeft == 0) {
            if (isWorkSession) {
                timerState = TimerState.BREAK
                timeLeft = breakSeconds
                isWorkSession = false
            } else {
                timerState = TimerState.IDLE
                isWorkSession = true
                onSessionComplete(0)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { onBackPressed() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.Gray
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = mood.emoji,
                            fontSize = 32.sp
                        )
                        Text(
                            text = mood.label,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Medium,
                            color = mood.color
                        )
                    }

                    IconButton(onClick = { showSettings = !showSettings }) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = Color.Gray
                        )
                    }
                }

                if (note.isNotEmpty()) {
                    Text(
                        text = note,
                        fontSize = 14.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Text(
                    text = if (isWorkSession) "Focus Time" else "Break Time",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isWorkSession) mood.color else Color(0xFF4CAF50)
                )

                Box(
                    modifier = Modifier
                        .size(280.dp)
                        .scale(if (timerState == TimerState.RUNNING) pulseScale else 1f)
                        .background(
                            color = Color.White,
                            shape = CircleShape
                        )
                        .border(
                            width = 8.dp,
                            color = if (isWorkSession) mood.color else Color(0xFF4CAF50),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = formatTime(timeLeft),
                        fontSize = 64.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF333333)
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FloatingActionButton(
                        onClick = {
                            when (timerState) {
                                TimerState.IDLE -> timerState = TimerState.RUNNING
                                TimerState.RUNNING -> timerState = TimerState.PAUSED
                                TimerState.PAUSED -> timerState = TimerState.RUNNING
                                TimerState.BREAK -> timerState = TimerState.RUNNING
                            }
                        },
                        containerColor = mood.color,
                        modifier = Modifier.size(72.dp)
                    ) {
                        Text(
                            text = if (timerState == TimerState.RUNNING) "⏸️" else "▶️",
                            fontSize = 36.sp
                        )
                    }

                    if (timerState != TimerState.IDLE) {
                        FloatingActionButton(
                            onClick = {
                                timerState = TimerState.IDLE
                                timeLeft = workSeconds
                                isWorkSession = true
                            },
                            containerColor = Color(0xFFEF5350),
                            modifier = Modifier.size(56.dp)
                        ) {
                            Text(
                                text = "⏹️",
                                fontSize = 28.sp
                            )
                        }
                    }
                }
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Focus Sounds",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF666666)
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    FocusSound.values().forEach { sound ->
                        SoundCard(
                            sound = sound,
                            isSelected = selectedSound == sound && isSoundEnabled,
                            onClick = {
                                if (selectedSound == sound && isSoundEnabled) {
                                    isSoundEnabled = false
                                } else {
                                    selectedSound = sound
                                    isSoundEnabled = true
                                }
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Study Music",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF666666)
                    )
                    TextButton(onClick = { showMusicDialog = true }) {
                        Text(
                            text = if (musicPlayer.currentTrack.value != null)
                                "Now Playing: ${musicPlayer.currentTrack.value?.name}"
                            else
                                "Browse Music",
                            fontSize = 12.sp
                        )
                    }
                }

                if (musicTracks.isNotEmpty()) {
                    if (musicPlayer.currentTrack.value != null) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFF667eea).copy(alpha = 0.1f)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = "🎵",
                                        fontSize = 20.sp
                                    )
                                    Text(
                                        text = musicPlayer.currentTrack.value?.name ?: "",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color(0xFF333333)
                                    )
                                }
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    IconButton(
                                        onClick = {
                                            if (musicPlayer.isPlaying.value) {
                                                musicPlayer.pause()
                                            } else {
                                                musicPlayer.currentTrack.value?.let {
                                                    musicPlayer.play(it)
                                                }
                                            }
                                        },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Text(
                                            text = if (musicPlayer.isPlaying.value) "⏸️" else "▶️",
                                            fontSize = 20.sp
                                        )
                                    }
                                    IconButton(
                                        onClick = { musicPlayer.stop() },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Text(
                                            text = "⏹️",
                                            fontSize = 20.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        if (showSettings) {
            SettingsDialog(
                currentWorkDuration = currentWorkDuration,
                currentBreakDuration = currentBreakDuration,
                onDismiss = { showSettings = false },
                onSave = { newWork, newBreak ->
                    currentWorkDuration = newWork
                    currentBreakDuration = newBreak
                    if (timerState == TimerState.IDLE && isWorkSession) {
                        timeLeft = if (newWork == 0) 5 else newWork * 60
                    }
                    showSettings = false
                }
            )
        }

        if (showMusicDialog) {
            MusicBrowserDialog(
                musicTracks = musicTracks,
                currentTrack = musicPlayer.currentTrack.value,
                onDismiss = { showMusicDialog = false },
                onTrackSelected = { track ->
                    musicPlayer.play(track)
                    showMusicDialog = false
                }
            )
        }
    }
}

@Composable
fun SoundCard(
    sound: FocusSound,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier
            .width(80.dp)
            .background(
                color = if (isSelected) Color(0xFF667eea).copy(alpha = 0.2f) else Color.White,
                shape = RoundedCornerShape(12.dp)
            )
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) Color(0xFF667eea) else Color(0xFFE0E0E0),
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick)
            .padding(12.dp)
    ) {
        Text(
            text = sound.emoji,
            fontSize = 28.sp
        )
        Text(
            text = sound.label,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) Color(0xFF667eea) else Color(0xFF666666)
        )
    }
}

@Composable
fun SettingsDialog(
    currentWorkDuration: Int,
    currentBreakDuration: Int,
    onDismiss: () -> Unit,
    onSave: (Int, Int) -> Unit
) {
    var workDuration by remember { mutableStateOf(currentWorkDuration.toString()) }
    var breakDuration by remember { mutableStateOf(currentBreakDuration.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Timer Settings",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = workDuration,
                    onValueChange = { workDuration = it },
                    label = { Text("Work Duration (minutes)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = breakDuration,
                    onValueChange = { breakDuration = it },
                    label = { Text("Break Duration (minutes)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val work = workDuration.toIntOrNull() ?: currentWorkDuration
                    val break_ = breakDuration.toIntOrNull() ?: currentBreakDuration
                    onSave(work, break_)
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun MusicBrowserDialog(
    musicTracks: List<MusicTrack>,
    currentTrack: MusicTrack?,
    onDismiss: () -> Unit,
    onTrackSelected: (MusicTrack) -> Unit
) {
    val context = LocalContext.current
    var showUploadMessage by remember { mutableStateOf(false) }
    var uploadMessage by remember { mutableStateOf("") }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            try {
                // Get the file name
                val fileName = getFileName(context.contentResolver, uri) ?: "music_${System.currentTimeMillis()}.mp3"
                val sanitizedFileName = fileName.lowercase().replace(" ", "_").replace(Regex("[^a-z0-9_.]"), "")

                // Copy file to app's music directory
                val musicDir = File(context.filesDir, "music")
                if (!musicDir.exists()) {
                    musicDir.mkdirs()
                }

                val destFile = File(musicDir, sanitizedFileName)
                context.contentResolver.openInputStream(uri)?.use { input ->
                    destFile.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }

                uploadMessage = "✅ Successfully added: ${fileName.removeSuffix(".mp3")}"
                showUploadMessage = true
            } catch (e: Exception) {
                uploadMessage = "❌ Failed to upload file: ${e.message}"
                showUploadMessage = true
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Study Music",
                    fontWeight = FontWeight.Bold
                )
                IconButton(
                    onClick = { filePickerLauncher.launch("audio/*") }
                ) {
                    Text(
                        text = "➕",
                        fontSize = 24.sp
                    )
                }
            }
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (showUploadMessage) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = if (uploadMessage.startsWith("✅"))
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                            else
                                MaterialTheme.colorScheme.error.copy(alpha = 0.1f)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = uploadMessage,
                                fontSize = 12.sp,
                                modifier = Modifier.weight(1f)
                            )
                            TextButton(onClick = { showUploadMessage = false }) {
                                Text("✕", fontSize = 16.sp)
                            }
                        }
                    }
                }

                if (musicTracks.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "🎵",
                        fontSize = 48.sp
                    )
                    Text(
                        text = "No music files found",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Tap ➕ to upload music files",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
            } else {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(musicTracks) { track ->
                        MusicTrackCard(
                            track = track,
                            isSelected = currentTrack?.let {
                                if (track.isFromFile) it.filePath == track.filePath
                                else it.resourceId == track.resourceId
                            } ?: false,
                            onClick = { onTrackSelected(track) }
                        )
                    }
                }
            }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        },
        containerColor = MaterialTheme.colorScheme.surface
    )
}

@Composable
fun MusicTrackCard(
    track: MusicTrack,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(150.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                Color(0xFF667eea).copy(alpha = 0.2f)
            else
                MaterialTheme.colorScheme.surface
        ),
        border = if (isSelected)
            androidx.compose.foundation.BorderStroke(2.dp, Color(0xFF667eea))
        else
            androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "🎵",
                fontSize = 40.sp
            )
            Text(
                text = track.name,
                fontSize = 14.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                maxLines = 2
            )
        }
    }
}

fun formatTime(seconds: Int): String {
    val minutes = seconds / 60
    val remainingSeconds = seconds % 60
    return String.format("%02d:%02d", minutes, remainingSeconds)
}

fun getFileName(contentResolver: ContentResolver, uri: Uri): String? {
    var fileName: String? = null
    val cursor = contentResolver.query(uri, null, null, null, null)
    cursor?.use {
        if (it.moveToFirst()) {
            val nameIndex = it.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
            if (nameIndex != -1) {
                fileName = it.getString(nameIndex)
            }
        }
    }
    return fileName
}
