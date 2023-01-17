@file:Suppress("MayBeConstant")

object Versions {
    val coil = "2.2.2"
    val glide = "4.14.2"

    object Sdk {
        val compileSdk = 33
        val targetSdk = 33
        val minSdk = 21
    }

    object Library {
        private val major = 1 // change when you make incompatible API changes
        private val minor = 6 // change when you add functionality in a backwards-compatible manner
        private val build = 0 // change when you make backwards-compatible bug fixes

        val versionCode = major * 10000 + minor * 100 + build
        val versionName = "${major}.${minor}.${build}"
    }

    object Kotlin {
        val stdLib = "1.7.21"
        val coroutines = "1.6.4"
    }

    object Google {
        object Android {
            val material = "1.7.0"
        }
    }

    object AndroidTools {
        val gradle = "7.4.0" // Attention! Version also must be changed in buildSrd/build.gradle
    }

    object AndroidX {
        val appcompat = "1.5.1"
        val coreKtx = "1.9.0"
        val constraintLayout = "2.1.4"
        val lifecycle = "2.5.1"
        val fragment = "1.5.5"
        val preference = "1.2.0"
        val window = "1.0.0"
    }
}