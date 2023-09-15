import com.samuelunknown.galleryImagePicker.Publishing

plugins {
    alias(libs.plugins.galleryImagePicker.library)
    alias(libs.plugins.galleryImagePicker.publish)
    alias(libs.plugins.google.ksp)
}

android {
    namespace = "com.samuelunknown.galleryImagePickerGlide"
}

dependencies {
    implementation(projects.galleryImagePicker)

    implementation(libs.glide)
    ksp(libs.glide.ksp)
}

configure<PublishConventionPluginExtension> {
    artifactId.set(Publishing.ArtifactIds.galleryImagePickerGlide)
}