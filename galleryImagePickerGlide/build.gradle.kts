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

val jarSources by tasks.registering(Jar::class) {
    archiveClassifier.set("sources")
    from(android.sourceSets.getByName("main").java.srcDirs)
}

configure<PublishConventionPluginExtension> {
    artifactId.set(Publishing.ArtifactIds.galleryImagePickerGlide)
    jar.set(jarSources)
}