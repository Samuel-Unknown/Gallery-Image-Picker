plugins {
    id(ANDROID_LIBRARY_PLUGIN)
    id(KOTLIN_ANDROID_PLUGIN)
    id(KOTLIN_KAPT_PLUGIN)
    id(MAVEN_PUBLISH_PLUGIN)
    id(SIGNING_PLUGIN)
}

android {
    applyConfig(isViewBindingEnabled = false)
    namespace = "com.samuelunknown.galleryImagePickerGlide"
}

dependencies {
    // Gallery Image Picker
    implementation(project(ProjectModules.galleryImagePicker))

    // Glide
    implementation(Libraries.Glide.glide)
    kapt(Libraries.Glide.compiler)
}

val sourcesJar by tasks.registering(Jar::class) {
    archiveClassifier.set("sources")
    from(android.sourceSets.getByName("main").java.srcDirs)
}

publishing(
    artifactId = Publishing.ArtifactIds.galleryImagePickerGlide,
    sourcesJar = sourcesJar
)