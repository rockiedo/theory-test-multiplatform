package com.rdev.tt.ui.components

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rdev.tt.ImagePathProvider
import com.rdev.tt._utils.Spacing
import com.rdev.tt.core_model.Category
import io.kamel.core.utils.File
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import org.koin.mp.KoinPlatformTools

@Composable
actual fun CustomImage(
    imageName: String,
    category: @Category String,
    modifier: Modifier
) {
    val pathProvider = KoinPlatformTools.defaultContext().get().get<ImagePathProvider>()
    val imageFile = remember(imageName) { File(pathProvider.getFullPath(imageName)) }

    KamelImage(
        resource = asyncPainterResource(data = imageFile, key = imageName),
        contentDescription = null,
        modifier = modifier.height(150.dp).padding(top = Spacing.x4)
    )
}