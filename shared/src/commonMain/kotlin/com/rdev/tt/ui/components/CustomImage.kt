package com.rdev.tt.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.rdev.tt.core_model.Category

@Composable
expect fun CustomImage(
    imageName: String,
    category: @Category String,
    modifier: Modifier = Modifier
)