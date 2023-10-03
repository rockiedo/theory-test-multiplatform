package com.rdev.tt._utils

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times

object Spacing {
    private val Base = 4.dp

    private fun scale(times: Int = 1) = times * Base

    val x = scale()
    val x2 = scale(2)
    val x4 = scale(4)
    val x6 = scale(6)
    val x8 = scale(8)
}

fun isValidImageName(name: String?): Boolean {
    name ?: return false
    if (name.trim().isEmpty()) return false
    return true
}