package com.rdev.tt.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.rdev.tt.TheoryTestVoyagerApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val systemUiController = rememberSystemUiController()

            TheoryTestVoyagerApp {
                systemUiController.setSystemBarsColor(it.surface)
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        // Ignored back gesture
    }
}
