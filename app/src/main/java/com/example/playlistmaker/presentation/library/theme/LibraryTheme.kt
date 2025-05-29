package com.example.playlistmaker.presentation.library.theme


import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import com.example.playlistmaker.R

private val LightLibraryColors = lightColorScheme(
    background = Color(0xFFFFFFFF),
    onBackground = Color(0xFF1C1C1C),
    surface = Color(0xFF1A1B22),
    onSurface = Color(0xFF000000),
    primary = Color(0xFF000000),
    secondary = Color(0xFFEEEEEE),
    onPrimary = Color(0xFFFFFFFF),
    tertiary = Color(0xFFAEAFB4)
)

private val DarkLibraryColors = darkColorScheme(
    background = Color(0xFF1A1B22),
    onBackground = Color(0xFFFFFFFF),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFFE0E0E0),
    primary = Color(0xFFFFFFFF),
    secondary = Color(0xFF2A2A2A),
    onPrimary = Color(0xFFFFFFFF),
    tertiary = Color(0xFF9A9A9A)
)

private val LibraryTypography = Typography(
    bodySmall = TextStyle(
        fontFamily = FontFamily(Font(R.font.ys_display_regular)),
        fontSize = 12.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily(Font(R.font.ys_display_regular)),
        fontSize = 16.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = FontFamily(Font(R.font.ys_display_regular)),
        fontSize = 22.sp
    )
)

@Composable
fun LibraryTheme(
    darkTheme: Boolean,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkLibraryColors else LightLibraryColors,
        typography = LibraryTypography,
        content = content
    )
}
