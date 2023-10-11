package com.rdev.tt.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.rdev.tt.TheoryTestApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TheoryTestApp()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        // Ignored back gesture
    }
}
