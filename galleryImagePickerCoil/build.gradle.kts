plugins {
    id(ANDROID_LIBRARY_PLUGIN)
    id(KOTLIN_ANDROID_PLUGIN)
    id(MAVEN_PUBLISH_PLUGIN)
    id(SIGNING_PLUGIN)
}

android {
    applyConfig(isViewBindingEnabled = false)
    namespace = "com.samuelunknown.galleryImagePickerCoil"
}

dependencies {
    // Gallery Image Picker
    implementation(project(ProjectModules.galleryImagePicker))

    // Coil
    implementation(Libraries.coil)
}

val sourcesJar by tasks.registering(Jar::class) {
    archiveClassifier.set("sources")
    from(android.sourceSets.getByName("main").java.srcDirs)
}

publishing(
    artifactId = Publishing.ArtifactIds.galleryImagePickerCoil,
    sourcesJar = sourcesJar
)