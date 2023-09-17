// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.galleryImagePicker.gradle.plugin)
    alias(libs.plugins.galleryImagePicker.application) apply false
    alias(libs.plugins.galleryImagePicker.library) apply false
    alias(libs.plugins.galleryImagePicker.publish) apply false
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.google.ksp) apply false
    alias(libs.plugins.kotlin.parcelize) apply false
}

tasks.register("clean", Delete::class) {
    delete(rootProject.layout.buildDirectory)
}