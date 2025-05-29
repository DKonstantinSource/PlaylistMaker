package com.example.playlistmaker.presentation.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.playlistmaker.R
import com.example.playlistmaker.presentation.view_model.settings.SettingsViewModel

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onShareClick: () -> Unit,
    onSupportClick: () -> Unit,
    onTermsClick: () -> Unit
) {
    val isDarkTheme by viewModel.themePreference.observeAsState(initial = false)

    SettingsTheme(darkTheme = isDarkTheme) {
        SettingsContent(
            isDarkTheme = isDarkTheme,
            onThemeSwitchChanged = { viewModel.setTheme(it) },
            onShareClick = onShareClick,
            onSupportClick = onSupportClick,
            onTermsClick = onTermsClick
        )
    }
}


@Composable
fun SettingsContent(
    isDarkTheme: Boolean,
    onThemeSwitchChanged: (Boolean) -> Unit,
    onShareClick: () -> Unit,
    onSupportClick: () -> Unit,
    onTermsClick: () -> Unit
) {
    val customFont = FontFamily(Font(R.font.ys_display_regular))
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .systemBarsPadding()
    ) {
        Text(
            text = stringResource(R.string.settings),
            style = MaterialTheme.typography.headlineMedium.copy(
                color = MaterialTheme.colorScheme.onSurface,
            ),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onThemeSwitchChanged(!isDarkTheme) },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.darkTheme),
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 16.sp,
                    fontFamily = customFont
                ),

                modifier = Modifier.weight(1f)
            )
            Switch(
                checked = isDarkTheme,
                onCheckedChange = onThemeSwitchChanged,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colorScheme.onSecondary,
                    uncheckedThumbColor = MaterialTheme.colorScheme.primary,
                    checkedTrackColor = MaterialTheme.colorScheme.secondary,
                    uncheckedTrackColor = MaterialTheme.colorScheme.onPrimary,
                )
            )
        }



        SettingsItem(stringResource(R.string.shareApp), Icons.Default.Share, onShareClick)

        SettingsItem(stringResource(R.string.supportMail), Icons.Default.Email, onSupportClick)

        SettingsItem(stringResource(R.string.termsOfUse), Icons.Default.ChevronRight, onTermsClick)
    }
}


@Composable
fun SettingsItem(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit
) {

    val customFont = FontFamily(Font(R.font.ys_display_regular))

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge.copy(
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 16.sp,
                fontFamily = customFont
            ),
            modifier = Modifier.weight(1f)
        )
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.tertiary

        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SettingsContentPreview() {
    SettingsContent(
        isDarkTheme = false,
        onThemeSwitchChanged = {},
        onShareClick = {},
        onSupportClick = {},
        onTermsClick = {}
    )
}
