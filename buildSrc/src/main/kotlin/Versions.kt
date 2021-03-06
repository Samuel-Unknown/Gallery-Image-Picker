@file:Suppress("MayBeConstant")

object Versions {
    val coil = "2.0.0-alpha06"
    val glide = "4.12.0"

    object Sdk {
        val compileSdk = 31
        val targetSdk = 31
        val minSdk = 21
    }

    object Library {
        private val major = 1 // change when you make incompatible API changes
        private val minor = 3 // change when you add functionality in a backwards-compatible manner
        private val build = 0 // change when you make backwards-compatible bug fixes

        val versionCode = major * 10000 + minor * 100 + build
        val versionName = "${major}.${minor}.${build}"
    }

    object Kotlin {
        val stdLib = "1.6.10"
        val coroutines = "1.6.0"
    }

    object Google {
        object Android {
            val material = "1.4.0"
        }
    }

    object AndroidTools {
        val gradle = "7.0.4" // Attention! Version also must be changed in buildSrd/build.gradle
    }

    object AndroidX {
        val appcompat = "1.4.0"
        val coreKtx = "1.7.0"
        val constraintLayout = "2.1.2"
        val lifecycle = "2.4.0"
        val fragment = "1.4.0"
        val preference = "1.1.1"
        val window = "1.0.0-rc01"
    }
}