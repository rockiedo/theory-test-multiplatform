package com.rdev.tt.ui.components

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.rdev.tt._utils.Spacing

@Composable
actual fun CustomImage(
    imageName: String,
    modifier: Modifier
) {
    AsyncImage(
        model = getImageFilePath(imageName),
        contentDescription = null,
        modifier = modifier.height(150.dp).padding(top = Spacing.x6)
    )
}

private fun getImageFilePath(imageName: String): String {
    return "file:///android_asset/images/$imageName.webp"
}