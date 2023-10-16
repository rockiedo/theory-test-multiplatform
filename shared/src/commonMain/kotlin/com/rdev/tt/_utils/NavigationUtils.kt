package com.rdev.tt._utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import cafe.adriel.voyager.navigator.Navigator
import com.rdev.tt.ui.NavigationBus
import org.koin.mp.KoinPlatformTools

fun Navigator.safePop() {
    if (canPop) pop()
}

@Composable
fun navigationBus(): NavigationBus {
    return remember { KoinPlatformTools.defaultContext().get().get() }
}