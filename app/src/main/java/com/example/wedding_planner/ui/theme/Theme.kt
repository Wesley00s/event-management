package com.example.wedding_planner.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = SageGreen80,
    onPrimary = Color(0xFF073824), // Texto escuro sobre botão verde claro
    primaryContainer = Color(0xFF224B36),
    onPrimaryContainer = Color(0xFFA1D5B6),

    secondary = Champagne80,
    onSecondary = Color(0xFF402D00),
    secondaryContainer = Color(0xFF5D4300),
    onSecondaryContainer = Color(0xFFFFDFA6),

    tertiary = DustyRose80,
    onTertiary = Color(0xFF561D34),

    background = DarkBackground,
    surface = DarkSurface,
    onBackground = Color(0xFFE1E3DF),
    onSurface = Color(0xFFE1E3DF)
)

private val LightColorScheme = lightColorScheme(
    primary = SageGreen40,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFBCF2D1),
    onPrimaryContainer = Color(0xFF002112),

    secondary = Champagne40,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFDFA6),
    onSecondaryContainer = Color(0xFF261A00),

    tertiary = DustyRose40,
    onTertiary = Color.White,

    background = OffWhite,
    surface = LightSurface,
    onBackground = Color(0xFF191C1A),
    onSurface = Color(0xFF191C1A)
)

@Composable
fun WeddingPlannerTheme(
    darkTheme: Boolean = true,
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}