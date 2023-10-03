plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("com.android.library")
    id("org.jetbrains.compose")
    id("app.cash.sqldelight")
}

@OptIn(org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi::class)
kotlin {
    targetHierarchy.default()
    jvm("desktop")
    androidTarget()
    
    sourceSets {
        val commonMain by getting {
            dependencies {
                @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
                api(compose.components.resources)
                api(compose.runtime)
                api(compose.foundation)
                api(compose.material3)
                api(compose.materialIconsExtended)

                api(libs.bundles.ktor)
                api(libs.kotlinSerialization)
                api(libs.koinCore)
                api(libs.bundles.mokoMvvm)
                api(libs.windowSizeClass)
            }
        }
        val androidMain by getting {
            dependencies {
                api(libs.koinAndroid)
                implementation(libs.coilCompose)
                implementation(libs.sqlDelightAndroid)
            }
        }
        val desktopMain by getting {
            dependencies {
                // More info, https://github.com/JetBrains/compose-multiplatform/releases/tag/v1.1.1
                implementation(libs.coroutinesJvm)
                implementation(libs.ktorJvm)
                implementation(libs.sqlDelightJvm)
            }
        }
    }
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(11))
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
    }
}

android {
    namespace = "com.rdev.tt"
    compileSdk = 34
    defaultConfig {
        minSdk = 24
    }
}

sqldelight {
    databases {
        create("AppDb") {
            packageName.set("com.rdev.tt.data.database")
        }
    }
}