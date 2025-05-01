package com.example.projecthub.ui.theme

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
import com.example.projecthub.viewModel.ThemeViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
//private val DarkColorScheme = darkColorScheme(
//    primary = Purple80,
//    secondary = PurpleGrey80,
//    tertiary = Pink80
//)
//
//private val LightColorScheme = lightColorScheme(
//    primary = Purple40,
//    secondary = PurpleGrey40,
//    tertiary = Pink40
//
//private val DarkColorScheme = darkColorScheme(
//    primary = DeepBlueGray,
//    onPrimary = OffWhite,
//    primaryContainer = CharcoalBlue,
//    onPrimaryContainer = OffWhite,
//    secondary = CadetBlue,
//    onSecondary = OffWhite,
//    secondaryContainer = CadetBlue.copy(alpha = 0.3f),
//    onSecondaryContainer = OffWhite,
//    tertiary = SlateGrayBlue,
//    onTertiary = Color.White,
//    tertiaryContainer = SlateGrayBlue.copy(alpha = 0.2f),
//    onTertiaryContainer = OffWhite,
//    background = CharcoalBlue.copy(alpha = 0.9f),
//    onBackground = OffWhite,
//    surface = CharcoalBlue.copy(alpha = 0.8f),
//    onSurface = OffWhite,
//    surfaceVariant = CharcoalBlue.copy(alpha = 0.4f),
//    onSurfaceVariant = LightGray,
//    outline = MediumGray,
//    error = Color(0xFFCF6679),
//    onError = Color.White,
//    errorContainer = Color(0xFF8B1D2C),
//    onErrorContainer = Color(0xFFFFDAD6)
//)
//
//private val LightColorScheme = lightColorScheme(
//    primary = ClassicSlateGray,
//    onPrimary = Color.White,
//    primaryContainer = CadetBlue.copy(alpha = 0.15f),
//    onPrimaryContainer = CharcoalBlue,
//    secondary = CadetBlue,
//    onSecondary = Color.White,
//    secondaryContainer = CadetBlue.copy(alpha = 0.1f),
//    onSecondaryContainer = CharcoalBlue,
//    tertiary = SlateGrayBlue,
//    onTertiary = Color.White,
//    tertiaryContainer = SlateGrayBlue.copy(alpha = 0.1f),
//    onTertiaryContainer = CharcoalBlue,
//    background = OffWhite,
//    onBackground = CharcoalBlue,
//    surface = LightGray,
//    onSurface = CharcoalBlue,
//    surfaceVariant = LightGray,
//    onSurfaceVariant = CharcoalBlue.copy(alpha = 0.7f),
//    outline = MediumGray,
//    error = Color(0xFFB00020),
//    onError = Color.White,
//    errorContainer = Color(0xFFFFDAD6),
//    onErrorContainer = Color(0xFF410002)
//)
private val DarkColorScheme = darkColorScheme(
    primary = StandardGold,
    onPrimary = PureBlack,
    primaryContainer = RichGold,
    onPrimaryContainer = OffWhite,
    secondary = MediumGold,
    onSecondary = DarkBlack,
    secondaryContainer = DeepGold.copy(alpha = 0.3f),
    onSecondaryContainer = OffWhite,
    tertiary = AccentGold,
    onTertiary = Color.Black,
    tertiaryContainer = DeepGold.copy(alpha = 0.2f),
    onTertiaryContainer = OffWhite,
    background = DarkestBlack,
    onBackground = OffWhite,
    surface = DarkBlack,
    onSurface = OffWhite,
    surfaceVariant = SoftBlack.copy(alpha = 0.4f),
    onSurfaceVariant = LightGray,
    outline = SoftGray,
    error = ErrorRed,
    onError = Color.White,
    errorContainer = Color(0xFF8B1D2C),
    onErrorContainer = Color(0xFFFFDAD6)
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF9C7C38),          // Softer gold
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFF8E1),  // Light gold container
    onPrimaryContainer = Color(0xFF5D4200),  // Dark brown for text on gold

    // Secondary colors - complementary to primary
    secondary = Color(0xFF795548),        // Warm brown
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFE0B2), // Light peach container
    onSecondaryContainer = Color(0xFF4E342E), // Dark brown for text

    // Tertiary colors - accent
    tertiary = Color(0xFF7E57C2),         // Muted purple
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFEDE7F6), // Light purple container
    onTertiaryContainer = Color(0xFF4527A0), // Dark purple for text

    // Backgrounds
    background = Color(0xFFFAFAFA),        // Very light gray
    onBackground = Color(0xFF212121),      // Very dark gray
    surface = Color(0xFFFFFFFF),           // Pure white
    onSurface = Color(0xFF212121),         // Very dark gray

    // Surface variants
    surfaceVariant = Color(0xFFF5F5F5),    // Light gray
    onSurfaceVariant = Color(0xFF616161),  // Medium-dark gray

    // Other UI elements
    outline = Color(0xFFBDBDBD),           // Light gray outline
    outlineVariant = Color(0xFF9E9E9E),    // Slightly darker gray outline

    // Error states
    error = Color(0xFFB00020),             // Standard Material error red
    onError = Color.White,
    errorContainer = Color(0xFFFFDAD6),    // Light red
    onErrorContainer = Color(0xFF410002),  // Dark red

    // Scrim and inverse surfaces
    scrim = Color(0x52000000),             // Semi-transparent black
    inverseSurface = Color(0xFF121212),    // Very dark gray
    inverseOnSurface = Color(0xFFFFFFFF),  // White

    // Surface tint color
    surfaceTint = Color(0xFF9C7C38).copy(alpha = 0.1f) // Slight gold tint
)


    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */

@Composable
fun ProjectHUBTheme(
    themeViewModel: ThemeViewModel,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val isDarkTheme by themeViewModel.isDarkMode.collectAsState()

    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (isDarkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        isDarkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}