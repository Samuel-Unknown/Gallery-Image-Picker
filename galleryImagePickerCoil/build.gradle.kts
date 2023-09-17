import com.samuelunknown.galleryImagePicker.Publishing

plugins {
    alias(libs.plugins.galleryImagePicker.library)
    alias(libs.plugins.galleryImagePicker.publish)
}

android {
    namespace = "com.samuelunknown.galleryImagePickerCoil"
}

dependencies {
    implementation(projects.galleryImagePicker)

    implementation(libs.coil)
}

configure<PublishConventionPluginExtension> {
    artifactId.set(Publishing.ArtifactIds.galleryImagePickerCoil)
}