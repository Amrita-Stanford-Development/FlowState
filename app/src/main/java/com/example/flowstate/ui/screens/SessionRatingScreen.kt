package com.example.flowstate.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flowstate.model.Mood

@Composable
fun SessionRatingScreen(
    mood: Mood,
    onRatingSelected: (Int) -> Unit
) {
    var selectedRating by remember { mutableStateOf<Int?>(null) }

    val ratingEmojis = listOf(
        1 to "😵",
        2 to "😕",
        3 to "😐",
        4 to "🙂",
        5 to "😄"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Great work! 🎉",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF333333),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(
            text = "How was your session?",
            fontSize = 20.sp,
            color = Color(0xFF666666),
            modifier = Modifier.padding(bottom = 40.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ratingEmojis.forEach { (rating, emoji) ->
                RatingButton(
                    emoji = emoji,
                    rating = rating,
                    isSelected = selectedRating == rating,
                    onClick = { selectedRating = rating },
                    color = mood.color
                )
            }
        }

        Spacer(modifier = Modifier.height(48.dp))

        Button(
            onClick = {
                selectedRating?.let { onRatingSelected(it) }
            },
            enabled = selectedRating != null,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = mood.color,
                disabledContainerColor = Color.LightGray
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = "Continue",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Skip",
            fontSize = 16.sp,
            color = Color.Gray,
            modifier = Modifier.clickable { onRatingSelected(0) }
        )
    }
}

@Composable
fun RatingButton(
    emoji: String,
    rating: Int,
    isSelected: Boolean,
    onClick: () -> Unit,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .size(70.dp)
            .background(
                color = if (isSelected) color.copy(alpha = 0.2f) else Color.White,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick)
            .padding(8.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = emoji,
            fontSize = 36.sp
        )
        if (isSelected) {
            Text(
                text = rating.toString(),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}
