package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
  primary = SleekPrimary,
  onPrimary = SleekOnPrimary,
  primaryContainer = SleekPrimaryContainer,
  onPrimaryContainer = SleekOnPrimaryContainer,
  secondary = SleekDarkSurfaceVariant,
  onSecondary = SleekTextPrimary,
  secondaryContainer = SleekDarkSurface,
  onSecondaryContainer = SleekPrimary,
  tertiary = SleekPenalty,
  onTertiary = SleekOnPenalty,
  background = SleekDarkBackground,
  onBackground = SleekTextPrimary,
  surface = SleekDarkSurface,
  onSurface = SleekTextPrimary,
  surfaceVariant = SleekDarkSurfaceVariant,
  onSurfaceVariant = SleekTextSecondary,
  outline = SleekDarkSurfaceVariant,
  error = SleekPenalty,
  onError = SleekOnPenalty
)

private val LightColorScheme = lightColorScheme(
  primary = SleekLightPrimary,
  onPrimary = SleekLightOnPrimary,
  primaryContainer = Color(0xFFEADDFF),
  onPrimaryContainer = Color(0xFF21005D),
  secondary = SleekLightSurfaceVariant,
  onSecondary = Color(0xFF1D1B20),
  secondaryContainer = Color(0xFFE8DEF8),
  onSecondaryContainer = Color(0xFF1D192B),
  tertiary = Color(0xFF7D5260),
  onTertiary = Color.White,
  background = SleekLightBackground,
  onBackground = Color(0xFF1D1B20),
  surface = SleekLightSurface,
  onSurface = Color(0xFF1D1B20),
  surfaceVariant = SleekLightSurfaceVariant,
  onSurfaceVariant = Color(0xFF49454F),
  outline = SleekLightOutline,
  error = Color(0xFFBA1A1A),
  onError = Color.White
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}
