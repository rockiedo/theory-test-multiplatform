package com.rdev.tt.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.reflect.KClass

@Stable
class NavController<T>(initial: T) {
    private val _currentNavItem = MutableStateFlow(initial)
    val currentNavItem: T
        @Composable get() = _currentNavItem.collectAsState().value

    fun navTo(dest: T) {
        _currentNavItem.value = dest
    }
}

@Composable
fun <T: Any> rememberNavController(clazz: KClass<T>, initial: T): NavController<T> {
    return remember(clazz) { NavController(initial) }
}