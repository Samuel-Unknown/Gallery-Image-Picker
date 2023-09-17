import java.util.Properties
import java.io.FileReader

import com.samuelunknown.galleryImagePicker.Keystore
import com.samuelunknown.galleryImagePicker.LibraryVersion

plugins {
    alias(libs.plugins.galleryImagePicker.application)
}

android {
    namespace = "com.samuelunknown.sample"

    defaultConfig {
        applicationId = "com.samuelunknown.gallery_image_picker_sample"

        versionCode = LibraryVersion.versionCode
        versionName = LibraryVersion.versionName

        vectorDrawables.useSupportLibrary = true
        buildFeatures {
            viewBinding = true
            buildConfig = true
        }
    }

    signingConfigs {
        create("release") {
            val keystorePropertiesFile = rootProject.file(Keystore.Files.Release.properties)
            if (keystorePropertiesFile.exists()) {
                val keystoreProperties = Properties()
                FileReader(keystorePropertiesFile).use { reader -> keystoreProperties.load(reader) }
                storePassword = keystoreProperties.getProperty(Keystore.Properties.storePassword)
                keyAlias = keystoreProperties.getProperty(Keystore.Properties.keyAlias)
                keyPassword = keystoreProperties.getProperty(Keystore.Properties.keyPassword)
            } else {
                println("File $keystorePropertiesFile doesn't exist")
            }

            val keystoreFile = rootProject.file(Keystore.Files.Release.jks)
            if (keystoreFile.exists()) {
                storeFile = keystoreFile
            } else {
                println("File $keystoreFile doesn't exist")
            }
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

    val glideFlavor = "Glide"
    val coil = "Coil"
    val imageLibraryDimension = "image_library_implementation"
    flavorDimensions += imageLibraryDimension
    productFlavors {
        create(glideFlavor) {
            dimension = imageLibraryDimension
            applicationIdSuffix = ".$glideFlavor"
        }
        create(coil) {
            dimension = imageLibraryDimension
            applicationIdSuffix = ".$coil"
        }
    }
}

dependencies {
    implementation(projects.galleryImagePicker)
    implementation(projects.galleryImagePickerGlide)
    implementation(projects.galleryImagePickerCoil)

    implementation(libs.kotlin.stdlib)

    implementation(libs.kotlinx.coroutines.android)

    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.fragment)
    implementation(libs.androidx.lifecycle.runtime)

    implementation(libs.google.android.material)
}