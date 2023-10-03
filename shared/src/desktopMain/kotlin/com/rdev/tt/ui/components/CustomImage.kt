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
import com.rdev.tt.core_model.Category
import java.io.File

@Composable
actual fun CustomImage(
    imageName: String,
    category: @Category String,
    modifier: Modifier
) {
    val filePath = getImageFilePath(imageName, category)

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

private fun getImageFilePath(imageName: String, category: @Category String): String {
    return ".assets/$category/images/$imageName.webp"
}

private fun imageFromFile(file: File): ImageBitmap {
    return org.jetbrains.skia.Image.makeFromEncoded(file.readBytes()).toComposeImageBitmap()
}