package com.example.playlistmaker.presentation.search


import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import com.example.playlistmaker.R

private val LightSearchColors = lightColorScheme(
    background = Color(0xFFFFFFFF),
    onBackground = Color(0xFF1C1C1C),
    surface = Color(0xFFAEAFB4), //Text Gray
    onSurface = Color(0xFF000000), // Text Title
    primary = Color(0xFF1A1B22), //You Search History and Backgrpund Button
    secondary = Color(0xFFFFFFFF), //TextButton
    onPrimary = Color(0xFFE6E8EB), //Text field Background
    tertiary = Color(0xFF1A1B22), //Text Field
    surfaceTint = Color(0xFFAEAFB4), //Icon Tint search field
)

private val DarkSearchColors = darkColorScheme(
    background = Color(0xFF1A1B22),
    onBackground = Color(0xFFFFFFFF),
    surface = Color(0xFFFFFFFF),  //Text Gray
    onSurface = Color(0xFFFFFFFF), // Text Title
    primary = Color(0xFFFFFFFF),  //You Search History and Backgrpund Button
    secondary = Color(0xFF1A1B22), //TextButton
    onPrimary = Color(0xFFFFFFFF), //Text field Background
    tertiary = Color(0xFF1A1B22),  //Text Field
    surfaceTint = Color(0xFF1A1B22), //Icon Tint search field
)

private val SearchTypography = Typography(
    titleMedium = TextStyle(
        fontFamily = FontFamily(Font(R.font.ys_display_medium)),
        fontSize = 18.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily(Font(R.font.ys_display_regular)),
        fontSize = 16.sp
    )
)

@Composable
fun SearchTheme(
    darkTheme: Boolean,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkSearchColors else LightSearchColors,
        typography = SearchTypography,
        content = content
    )
}
