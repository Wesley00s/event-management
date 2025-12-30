package com.example.wedding_planner.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color

@Composable
fun BackgroundDecorations(color: Color) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        drawCircle(
            color = color,
            center = Offset(x = width * 0.1f, y = height * 0.15f),
            radius = 150f
        )

        drawCircle(
            color = color,
            center = Offset(x = width * 0.9f, y = height * 0.05f),
            radius = 280f
        )
    }
}