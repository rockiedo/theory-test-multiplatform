package com.rdev.tt.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.unit.dp
import com.rdev.tt._utils.Spacing
import java.io.File

@Composable
actual fun CustomImage(
    imageName: String,
    modifier: Modifier
) {
    val filePath = getImageFilePath(imageName)

    if (!File(filePath).exists()) {
        // TODO: report missing image
        return
    }

    val image = remember(filePath) { imageFromFile(File(filePath)) }

    Image(
        image,
        null,
        modifier
            .height(250.dp)
            .padding(top = Spacing.x6)
    )
}

private fun getImageFilePath(imageName: String): String {
    return ".assets/images/$imageName.webp"
}

private fun imageFromFile(file: File): ImageBitmap {
    return org.jetbrains.skia.Image.makeFromEncoded(file.readBytes()).toComposeImageBitmap()
}