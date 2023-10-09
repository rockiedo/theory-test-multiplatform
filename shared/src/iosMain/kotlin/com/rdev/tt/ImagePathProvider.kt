package com.rdev.tt

class ImagePathProvider(private val basePath: String) {
    fun getFullPath(imageName: String): String {
        return "${basePath}/${imageName}.webp"
    }
}