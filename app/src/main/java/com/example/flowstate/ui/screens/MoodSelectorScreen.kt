package com.example.flowstate.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flowstate.model.Mood
import com.example.flowstate.model.TimerPreset
import com.example.flowstate.util.QuotesProvider

@Composable
fun MoodSelectorScreen(
    onMoodSelected: (Mood, String, Int, Int) -> Unit,
    onBackPressed: () -> Unit = {}
) {
    var selectedMood by remember { mutableStateOf<Mood?>(null) }
    var note by remember { mutableStateOf("") }
    var selectedPreset by remember { mutableStateOf(TimerPreset.DEFAULT_PRESETS[0]) }
    var showPresetDialog by remember { mutableStateOf(false) }
    var showCustomDialog by remember { mutableStateOf(false) }
    var customWorkDuration by remember { mutableStateOf(25) }
    var customBreakDuration by remember { mutableStateOf(5) }
    var isCustomTimer by remember { mutableStateOf(false) }
    val quote = remember { QuotesProvider.getDailyQuote() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.Start
        ) {
            IconButton(onClick = { onBackPressed() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF667eea).copy(alpha = 0.1f)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "💭",
                    fontSize = 24.sp
                )
                Text(
                    text = "\"${quote.first}\"",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "- ${quote.second}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "How are you feeling?",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Mood Grid
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                MoodCard(
                    mood = Mood.ENERGIZED,
                    isSelected = selectedMood == Mood.ENERGIZED,
                    onClick = { selectedMood = Mood.ENERGIZED },
                    modifier = Modifier.weight(1f)
                )
                MoodCard(
                    mood = Mood.CALM,
                    isSelected = selectedMood == Mood.CALM,
                    onClick = { selectedMood = Mood.CALM },
                    modifier = Modifier.weight(1f)
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                MoodCard(
                    mood = Mood.STRESSED,
                    isSelected = selectedMood == Mood.STRESSED,
                    onClick = { selectedMood = Mood.STRESSED },
                    modifier = Modifier.weight(1f)
                )
                MoodCard(
                    mood = Mood.TIRED,
                    isSelected = selectedMood == Mood.TIRED,
                    onClick = { selectedMood = Mood.TIRED },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Timer Preset",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showPresetDialog = true },
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isCustomTimer) "⚙️" else selectedPreset.emoji,
                        fontSize = 28.sp
                    )
                    Column {
                        Text(
                            text = if (isCustomTimer) "Custom Timer" else selectedPreset.name,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (isCustomTimer) {
                                "${customWorkDuration}min work / ${customBreakDuration}min break"
                            } else if (selectedPreset.workDuration == 0) {
                                "5s work / 5s break"
                            } else {
                                "${selectedPreset.workDuration}min work / ${selectedPreset.breakDuration}min break"
                            },
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Text(
                    text = "⏱️",
                    fontSize = 24.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = note,
            onValueChange = { note = it },
            label = { Text("Add a note (optional)") },
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp),
            maxLines = 2,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = selectedMood?.color ?: Color.Gray,
                focusedLabelColor = selectedMood?.color ?: Color.Gray
            )
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                selectedMood?.let { mood ->
                    val workDuration = if (isCustomTimer) customWorkDuration else selectedPreset.workDuration
                    val breakDuration = if (isCustomTimer) customBreakDuration else selectedPreset.breakDuration
                    onMoodSelected(mood, note, workDuration, breakDuration)
                }
            },
            enabled = selectedMood != null,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = selectedMood?.color ?: Color.Gray,
                disabledContainerColor = Color.LightGray
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = "Start Session",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }

    if (showPresetDialog) {
        TimerPresetDialog(
            presets = TimerPreset.DEFAULT_PRESETS,
            selectedPreset = selectedPreset,
            isCustomSelected = isCustomTimer,
            onDismiss = { showPresetDialog = false },
            onPresetSelected = { preset ->
                selectedPreset = preset
                isCustomTimer = false
                showPresetDialog = false
            },
            onCustomSelected = {
                showPresetDialog = false
                showCustomDialog = true
            }
        )
    }

    if (showCustomDialog) {
        CustomTimerDialog(
            initialWorkDuration = customWorkDuration,
            initialBreakDuration = customBreakDuration,
            onDismiss = { showCustomDialog = false },
            onSave = { work, break_ ->
                customWorkDuration = work
                customBreakDuration = break_
                isCustomTimer = true
                showCustomDialog = false
            }
        )
    }
}

@Composable
fun TimerPresetDialog(
    presets: List<TimerPreset>,
    selectedPreset: TimerPreset,
    isCustomSelected: Boolean,
    onDismiss: () -> Unit,
    onPresetSelected: (TimerPreset) -> Unit,
    onCustomSelected: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Choose Timer Preset",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                presets.forEach { preset ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onPresetSelected(preset) },
                        colors = CardDefaults.cardColors(
                            containerColor = if (selectedPreset == preset && !isCustomSelected)
                                Color(0xFF667eea).copy(alpha = 0.2f)
                            else
                                MaterialTheme.colorScheme.surface
                        ),
                        border = if (selectedPreset == preset && !isCustomSelected)
                            androidx.compose.foundation.BorderStroke(2.dp, Color(0xFF667eea))
                        else
                            null,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = preset.emoji, fontSize = 32.sp)
                            Column {
                                Text(
                                    text = preset.name,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = if (preset.workDuration == 0) {
                                        "5s work / 5s break"
                                    } else {
                                        "${preset.workDuration}min work / ${preset.breakDuration}min break"
                                    },
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                // Custom Timer Option
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onCustomSelected() },
                    colors = CardDefaults.cardColors(
                        containerColor = if (isCustomSelected)
                            Color(0xFF667eea).copy(alpha = 0.2f)
                        else
                            MaterialTheme.colorScheme.surface
                    ),
                    border = if (isCustomSelected)
                        androidx.compose.foundation.BorderStroke(2.dp, Color(0xFF667eea))
                    else
                        null,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "⚙️", fontSize = 32.sp)
                        Column {
                            Text(
                                text = "Custom Timer",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Set your own duration",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
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
fun MoodCard(
    mood: Mood,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .background(
                color = if (isSelected) mood.color.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(16.dp)
            )
            .border(
                width = if (isSelected) 3.dp else 1.dp,
                color = if (isSelected) mood.color else MaterialTheme.colorScheme.outline,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(onClick = onClick)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = mood.emoji,
                fontSize = 48.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = mood.label,
                fontSize = 16.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) mood.color else MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun CustomTimerDialog(
    initialWorkDuration: Int,
    initialBreakDuration: Int,
    onDismiss: () -> Unit,
    onSave: (Int, Int) -> Unit
) {
    var workDuration by remember { mutableStateOf(initialWorkDuration.toString()) }
    var breakDuration by remember { mutableStateOf(initialBreakDuration.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Custom Timer",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Set your custom work and break durations",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                OutlinedTextField(
                    value = workDuration,
                    onValueChange = {
                        if (it.isEmpty() || it.all { char -> char.isDigit() }) {
                            workDuration = it
                        }
                    },
                    label = { Text("Work Duration (minutes)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF667eea),
                        focusedLabelColor = Color(0xFF667eea)
                    )
                )

                OutlinedTextField(
                    value = breakDuration,
                    onValueChange = {
                        if (it.isEmpty() || it.all { char -> char.isDigit() }) {
                            breakDuration = it
                        }
                    },
                    label = { Text("Break Duration (minutes)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF667eea),
                        focusedLabelColor = Color(0xFF667eea)
                    )
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val work = workDuration.toIntOrNull() ?: initialWorkDuration
                    val break_ = breakDuration.toIntOrNull() ?: initialBreakDuration
                    if (work > 0 && break_ > 0) {
                        onSave(work, break_)
                    }
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        containerColor = MaterialTheme.colorScheme.surface
    )
}
