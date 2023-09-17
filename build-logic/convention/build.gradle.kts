plugins {
    `kotlin-dsl`
}

group = "com.samuelunknown.galleryImagePicker.build-logic"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
}

gradlePlugin {
    plugins {
        register("galleryImagePicker.gradle.plugin") {
            val plugin = libs.plugins.galleryImagePicker.gradle.plugin.get()
            id = plugin.pluginId
            version = plugin.version
            implementationClass = "GalleryImagePickerGradlePlugin"
        }

        register("galleryImagePicker.android.application") {
            val plugin = libs.plugins.galleryImagePicker.application.get()
            id = plugin.pluginId
            version = plugin.version
            implementationClass = "AndroidApplicationConventionPlugin"
        }

        register("galleryImagePicker.android.library") {
            val plugin = libs.plugins.galleryImagePicker.library.get()
            id = plugin.pluginId
            version = plugin.version
            implementationClass = "AndroidLibraryConventionPlugin"
        }

        register("galleryImagePicker.publish") {
            val plugin = libs.plugins.galleryImagePicker.publish.get()
            id = plugin.pluginId
            version = plugin.version
            implementationClass = "PublishConventionPlugin"
        }
    }
}