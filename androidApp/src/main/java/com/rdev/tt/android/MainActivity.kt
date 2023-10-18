package com.rdev.tt.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.rdev.tt.TheoryTestApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        actionBar?.hide()
        super.onCreate(savedInstanceState)

        setContent {
            val systemUiController = rememberSystemUiController()

            TheoryTestApp {
                systemUiController.setSystemBarsColor(it.surface)
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        // Ignored back gesture
    }
}
