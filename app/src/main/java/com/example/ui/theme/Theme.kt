package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val CyberDarkColorScheme = darkColorScheme(
  primary = ElectricCyan,
  onPrimary = DeepNavyOnPrimary,
  primaryContainer = CyanContainer,
  onPrimaryContainer = CyanContainerLight,
  secondary = NeonMint,
  onSecondary = DeepNavyOnPrimary,
  secondaryContainer = MintContainer,
  onSecondaryContainer = MintLight,
  tertiary = SolarAmber,
  onTertiary = DeepNavyOnPrimary,
  background = MidnightBackground,
  onBackground = TextPrimary,
  surface = SurfaceDark,
  onSurface = TextPrimary,
  surfaceVariant = SurfaceElevated,
  onSurfaceVariant = TextSecondary,
  outline = SurfaceBorderStrong,
  outlineVariant = SurfaceBorder,
  error = VibrantCoral,
  onError = CoralContainer
)

private val CyberLightColorScheme = lightColorScheme(
  primary = Color(0xFF0284C7),
  onPrimary = Color.White,
  primaryContainer = Color(0xFFE0F2FE),
  onPrimaryContainer = Color(0xFF0369A1),
  secondary = Color(0xFF059669),
  onSecondary = Color.White,
  tertiary = SolarAmber,
  background = Color(0xFFF8FAFC),
  onBackground = Color(0xFF0F172A),
  surface = Color(0xFFFFFFFF),
  onSurface = Color(0xFF0F172A),
  surfaceVariant = Color(0xFFF1F5F9),
  onSurfaceVariant = Color(0xFF475569),
  outline = Color(0xFF94A3B8),
  error = Color(0xFFE11D48),
  onError = Color.White
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true,
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit
) {
  val colorScheme = if (darkTheme) CyberDarkColorScheme else CyberLightColorScheme
  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}
