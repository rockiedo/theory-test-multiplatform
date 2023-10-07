plugins {
    kotlin("multiplatform")
    kotlin("native.cocoapods")
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
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    cocoapods {
        summary = "Some description for the Shared Module"
        homepage = "Link to the Shared Module homepage"
        version = "1.0"
        ios.deploymentTarget = "14.1"
        podfile = project.file("../iosApp/Podfile")
        framework {
            baseName = "shared"
            export(libs.bundles.mokoMvvm)
        }
    }
    
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
                api(libs.dateTime)
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
        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by getting {
            dependencies {
                implementation(libs.sqlDelightNative)
            }
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
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