package com.example.wedding_planner.ui.components


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

@Composable
fun HomeBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val backgroundColor = MaterialTheme.colorScheme.background
    val primaryColor = MaterialTheme.colorScheme.primaryContainer
    val secondaryColor = MaterialTheme.colorScheme.secondaryContainer

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor) 
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        primaryColor.copy(alpha = 0.35f), 
                        Color.Transparent
                    ),
                    center = Offset(x = 0f, y = 0f), 
                    radius = 900f 
                )
            )
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        secondaryColor.copy(alpha = 0.3f), 
                        Color.Transparent
                    ),
                    center = Offset(x = 1200f, y = 0f), 
                    radius = 700f
                )
            )
    ) {
        content()
    }
}