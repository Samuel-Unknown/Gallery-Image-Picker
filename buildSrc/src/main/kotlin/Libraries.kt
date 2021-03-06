@file:Suppress("MayBeConstant")

object Libraries {
    val coil = "io.coil-kt:coil:${Versions.coil}"

    object Glide {
        val glide = "com.github.bumptech.glide:glide:${Versions.glide}"
        val compiler = "com.github.bumptech.glide:compiler:${Versions.glide}"
    }

    object Kotlin {
        val stdLib = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:${Versions.Kotlin.stdLib}"
        val coroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.Kotlin.coroutines}"
    }

    object Google {
        object Android {
            val material = "com.google.android.material:material:${Versions.Google.Android.material}"
        }
    }

    object AndroidTools {
        val gradle = "com.android.tools.build:gradle:${Versions.AndroidTools.gradle}"
    }

    object AndroidX {
        val appcompat = "androidx.appcompat:appcompat:${Versions.AndroidX.appcompat}"
        val coreKtx = "androidx.core:core-ktx:${Versions.AndroidX.coreKtx}"
        val constraintLayout = "androidx.constraintlayout:constraintlayout:${Versions.AndroidX.constraintLayout}"
        val fragmentKtx = "androidx.fragment:fragment-ktx:${Versions.AndroidX.fragment}"
        val preference ="androidx.preference:preference-ktx:${Versions.AndroidX.preference}"
        val window = "androidx.window:window:${Versions.AndroidX.window}"

        object Lifecycle {
            val runtimeKtx = "androidx.lifecycle:lifecycle-runtime-ktx:${Versions.AndroidX.lifecycle}"
        }
    }
}