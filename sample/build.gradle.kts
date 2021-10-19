import java.util.Properties
import java.io.FileReader

plugins {
    id(ANDROID_APPLICATION_PLUGIN)
    id(KOTLIN_ANDROID_PLUGIN)
    id(KOTLIN_KAPT_PLUGIN)
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

    signingConfigs {
        create("release") {
            val keystorePropertiesFile = rootProject.file(Keystore.Files.Release.properties)
            val keystoreFile = rootProject.file(Keystore.Files.Release.jks)
            val keystoreProperties = Properties()
            FileReader(keystorePropertiesFile).use { reader -> keystoreProperties.load(reader) }

            storeFile = keystoreFile
            storePassword = keystoreProperties.getProperty(Keystore.Properties.storePassword)
            keyAlias = keystoreProperties.getProperty(Keystore.Properties.keyAlias)
            keyPassword = keystoreProperties.getProperty(Keystore.Properties.keyPassword)
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            signingConfig = signingConfigs.getByName("release")
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
    // Gallery Image Picker
    implementation(project(ProjectModules.galleryImagePicker))

    // Kotlin
    implementation(Libraries.Kotlin.stdLib)
    implementation(Libraries.Kotlin.coroutines)

    // Android X
    implementation(Libraries.AndroidX.appcompat)
    implementation(Libraries.AndroidX.coreKtx)
    implementation(Libraries.AndroidX.constraintLayout)
    implementation(Libraries.AndroidX.Lifecycle.runtimeKtx)

    // Material
    implementation(Libraries.Google.Android.material)

    // Glide
    implementation(Libraries.Glide.glide)
    kapt(Libraries.Glide.compiler)
}