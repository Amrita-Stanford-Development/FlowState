package com.example.flowstate.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun BreathingExerciseScreen(
    onComplete: () -> Unit
) {
    var breathingPhase by remember { mutableStateOf(BreathingPhase.INHALE) }
    var cyclesCompleted by remember { mutableIntStateOf(0) }
    val totalCycles = 3

    // Controlled animation target based on phase
    var animationTarget by remember { mutableFloatStateOf(1f) }

    val circleScale by animateFloatAsState(
        targetValue = animationTarget,
        animationSpec = when (breathingPhase) {
            BreathingPhase.INHALE -> tween(4000, easing = LinearEasing)
            BreathingPhase.HOLD -> tween(0, easing = LinearEasing) // No animation during hold
            BreathingPhase.EXHALE -> tween(4000, easing = LinearEasing)
            BreathingPhase.REST -> tween(2000, easing = LinearEasing)
        },
        label = "circleScale"
    )

    // Handle breathing cycle progression and animation sync
    LaunchedEffect(Unit) {
        while (cyclesCompleted < totalCycles) {
            // Inhale phase (4 seconds) - expand
            breathingPhase = BreathingPhase.INHALE
            animationTarget = 1f
            delay(4000)

            // Hold phase (4 seconds) - stay at full size
            breathingPhase = BreathingPhase.HOLD
            animationTarget = 1f // Keep it at full size
            delay(4000)

            // Exhale phase (4 seconds) - contract
            breathingPhase = BreathingPhase.EXHALE
            animationTarget = 0.5f
            delay(4000)

            // Rest phase (2 seconds) - stay small
            breathingPhase = BreathingPhase.REST
            animationTarget = 0.5f
            delay(2000)

            cyclesCompleted++
        }

        // Complete the exercise
        onComplete()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(32.dp)
        ) {
            // Title
            Text(
                text = "Take a Deep Breath",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = "Let's calm down together",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 48.dp)
            )

            // Breathing circle animation
            Box(
                modifier = Modifier
                    .size(280.dp)
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                // Animated breathing circle
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    val radius = size.minDimension / 2 * circleScale
                    drawCircle(
                        color = when (breathingPhase) {
                            BreathingPhase.INHALE -> Color(0xFF667eea)
                            BreathingPhase.HOLD -> Color(0xFF764ba2)
                            BreathingPhase.EXHALE -> Color(0xFF48c6ef)
                            BreathingPhase.REST -> Color(0xFF6BB6FF)
                        },
                        radius = radius,
                        alpha = 0.3f
                    )
                    drawCircle(
                        color = when (breathingPhase) {
                            BreathingPhase.INHALE -> Color(0xFF667eea)
                            BreathingPhase.HOLD -> Color(0xFF764ba2)
                            BreathingPhase.EXHALE -> Color(0xFF48c6ef)
                            BreathingPhase.REST -> Color(0xFF6BB6FF)
                        },
                        radius = radius * 0.8f,
                        alpha = 0.5f
                    )
                }

                // Phase text
                Text(
                    text = when (breathingPhase) {
                        BreathingPhase.INHALE -> "Breathe In"
                        BreathingPhase.HOLD -> "Hold"
                        BreathingPhase.EXHALE -> "Breathe Out"
                        BreathingPhase.REST -> "Rest"
                    },
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = when (breathingPhase) {
                        BreathingPhase.INHALE -> Color(0xFF667eea)
                        BreathingPhase.HOLD -> Color(0xFF764ba2)
                        BreathingPhase.EXHALE -> Color(0xFF48c6ef)
                        BreathingPhase.REST -> Color(0xFF6BB6FF)
                    },
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Progress indicator
            Text(
                text = "Cycle ${cyclesCompleted + 1} of $totalCycles",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Progress bar
            LinearProgressIndicator(
                progress = { (cyclesCompleted.toFloat() + when(breathingPhase) {
                    BreathingPhase.INHALE -> 0.25f
                    BreathingPhase.HOLD -> 0.5f
                    BreathingPhase.EXHALE -> 0.75f
                    BreathingPhase.REST -> 1f
                }) / totalCycles },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
                    .height(8.dp),
                color = Color(0xFF667eea),
                trackColor = Color(0xFF667eea).copy(alpha = 0.2f),
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Skip button
            TextButton(
                onClick = onComplete,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            ) {
                Text(
                    text = "Skip",
                    fontSize = 16.sp
                )
            }
        }
    }
}

enum class BreathingPhase {
    INHALE,
    HOLD,
    EXHALE,
    REST
}
