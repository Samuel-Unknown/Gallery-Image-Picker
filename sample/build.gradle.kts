plugins {
    id(ANDROID_APPLICATION_PLUGIN)
    id(KOTLIN_ANDROID_PLUGIN)
}

android {
    compileSdk = Versions.Sdk.compileSdk

    defaultConfig {
        applicationId = APPLICATION_ID
        testInstrumentationRunner = ANDROID_TEST_INSTRUMENTATION_RUNNER

        minSdk = Versions.Sdk.minSdk
        targetSdk = Versions.Sdk.targetSdk

        versionCode = Versions.Library.versionCode
        versionName = Versions.Library.versionName

        vectorDrawables.useSupportLibrary = true

        buildFeatures {
            viewBinding = true
        }

        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_11
            targetCompatibility = JavaVersion.VERSION_11
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            setProguardFiles(
                listOf(
                    getDefaultProguardFile("proguard-android-optimize.txt"),
                    file("proguard-rules.pro")
                )
            )
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