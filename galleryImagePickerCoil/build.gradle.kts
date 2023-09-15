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

val jarSources by tasks.registering(Jar::class) {
    archiveClassifier.set("sources")
    from(android.sourceSets.getByName("main").java.srcDirs)
}

configure<PublishConventionPluginExtension> {
    artifactId.set(Publishing.ArtifactIds.galleryImagePickerCoil)
    jar.set(jarSources)
}