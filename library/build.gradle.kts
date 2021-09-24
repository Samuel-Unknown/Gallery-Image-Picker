plugins {
    id(ANDROID_LIBRARY_PLUGIN)
    id(KOTLIN_ANDROID_PLUGIN)
}

android {
    compileSdk = Versions.Sdk.compileSdk

    defaultConfig {
        minSdk = Versions.Sdk.minSdk
        targetSdk = Versions.Sdk.targetSdk
        testInstrumentationRunner = ANDROID_TEST_INSTRUMENTATION_RUNNER

        buildFeatures {
            viewBinding = true
        }

        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_11
            targetCompatibility = JavaVersion.VERSION_11
        }
    }
}

dependencies {
    // Android X
    implementation(Libraries.AndroidX.appcompat)
    implementation(Libraries.AndroidX.coreKtx)
    implementation(Libraries.AndroidX.constraintLayout)

    // Material
    implementation(Libraries.Google.Android.material)
}