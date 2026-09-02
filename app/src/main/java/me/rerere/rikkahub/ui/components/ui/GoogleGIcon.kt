package me.rerere.rikkahub.ui.components.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// G icon warna Google untuk pill model - ringan, tanpa image asset
@Composable
fun GoogleGIcon(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "G",
            color = Color(0xFF4285F4),
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 14.sp
        )
    }
}

// Versi gradient Google 4 warna untuk highlight
@Composable
fun GoogleGIconGradient(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF4285F4), // blue
                        Color(0xFFEA4335), // red
                        Color(0xFFFBBC05), // yellow
                        Color(0xFF34A853), // green
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "G",
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.ExtraBold
        )
    }
}
