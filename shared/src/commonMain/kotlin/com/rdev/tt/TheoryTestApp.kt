package com.rdev.tt

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import com.rdev.tt._utils.DarkColors
import com.rdev.tt._utils.LightColors
import com.rdev.tt.ui.home.HomeScreen

@Composable
fun TheoryTestApp(
    darkTheme: Boolean = isSystemInDarkTheme(),
    setSystemBarsColor: @Composable (ColorScheme) -> Unit = {}
) {
    val colors = if (darkTheme) DarkColors else LightColors
    setSystemBarsColor(colors)

    MaterialTheme(
        colorScheme = colors,
        content = {
            Navigator(screen = HomeScreen) { navigator ->
                SlideTransition(navigator)
            }
        }
    )
}