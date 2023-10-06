package com.rdev.tt.desktop

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.singleWindowApplication
import com.rdev.tt.TheoryTestApp
import com.rdev.tt.data.dataModule
import com.rdev.tt.scripts.cliScript
import com.rdev.tt.ui.viewModelModule
import org.koin.core.context.startKoin

fun main() {
    cliScript()
    return

    startKoin {
        modules(dbDriverModule, dataModule, viewModelModule)
    }

    singleWindowApplication(
        title = "Theory Test",
        state = WindowState(size = DpSize(725.dp, 1200.dp))
    ) {
        MaterialTheme(
            typography = Typography()
        ) {
            TheoryTestApp()
        }
    }
}
