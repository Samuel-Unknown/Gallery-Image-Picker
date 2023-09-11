@file:Suppress("MayBeConstant")

object Versions {
    val coil = "2.4.0"
    val glide = "4.16.0"

    object Sdk {
        val compileSdk = 34
        val targetSdk = 34
        val minSdk = 21
    }

    object Library {
        private val major = 1 // change when you make incompatible API changes
        private val minor = 6 // change when you add functionality in a backwards-compatible manner
        private val build = 1 // change when you make backwards-compatible bug fixes

        val versionCode = major * 10000 + minor * 100 + build
        val versionName = "${major}.${minor}.${build}"
    }

    object Kotlin {
        val stdLib = "1.9.10"
        val coroutines = "1.7.3"
    }

    object Google {
        object Android {
            val material = "1.9.0"
        }
    }

    object AndroidTools {
        val gradle = "8.1.1" // Attention! Version also must be changed in buildSrd/build.gradle
    }

    object AndroidX {
        val appcompat = "1.6.1"
        val coreKtx = "1.12.0"
        val constraintLayout = "2.1.4"
        val lifecycle = "2.6.2"
        val fragment = "1.6.1"
        val preference = "1.2.0"
        val window = "1.1.0"
    }
}