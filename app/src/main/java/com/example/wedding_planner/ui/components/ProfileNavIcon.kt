package com.example.wedding_planner.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage

@Composable
fun ProfileNavIcon(
    photoUrl: String,
    isSelected: Boolean
) {
    val modifier = if (isSelected) {
        Modifier
            .size(26.dp) 
            .clip(CircleShape)
            .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
    } else {
        Modifier
            .size(24.dp)
            .clip(CircleShape)
    }

    AsyncImage(
        model = photoUrl,
        contentDescription = "Profile",
        modifier = modifier,
        contentScale = ContentScale.Crop
    )
}