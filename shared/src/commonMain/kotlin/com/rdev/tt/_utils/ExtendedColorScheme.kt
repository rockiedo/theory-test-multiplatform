package com.rdev.tt._utils

import androidx.compose.ui.graphics.Color

data class ExtendedColorScheme(
    val background: Color,
    val onBackground: Color,
    val border: Color
) {
    companion object {
        val Light = ExtendedColorScheme(
            background = Color(0xff206d00),
            onBackground = Color(0xffffffff),
            border = Color(0xff8afd60)
        )
        val Dark = ExtendedColorScheme(
            background = Color(0xff6fdf47),
            onBackground = Color(0xff0c3900),
            border = Color(0xff165200)
        )
    }
}