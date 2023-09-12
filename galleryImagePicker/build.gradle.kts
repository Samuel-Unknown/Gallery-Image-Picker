plugins {
    id(ANDROID_LIBRARY_PLUGIN)
    id(KOTLIN_ANDROID_PLUGIN)
    id(KOTLIN_PARCELIZE_PLUGIN)
    id(MAVEN_PUBLISH_PLUGIN)
    id(SIGNING_PLUGIN)
}

android {
    applyConfig(isViewBindingEnabled = true)
    namespace = "com.samuelunknown.galleryImagePicker"
}

dependencies {
    // Kotlin
    implementation(Libraries.Kotlin.stdLib)
    implementation(Libraries.Kotlin.coroutines)

    // Android X
    implementation(Libraries.AndroidX.appcompat)
    implementation(Libraries.AndroidX.coreKtx)
    implementation(Libraries.AndroidX.constraintLayout)
    implementation(Libraries.AndroidX.fragmentKtx)
    implementation(Libraries.AndroidX.preference)
    implementation(Libraries.AndroidX.window)
    implementation(Libraries.AndroidX.Lifecycle.runtimeKtx)

    // Material
    implementation(Libraries.Google.Android.material)
}

val sourcesJar by tasks.registering(Jar::class) {
    archiveClassifier.set("sources")
    from(android.sourceSets.getByName("main").java.srcDirs)
}

publishing(
    artifactId = Publishing.ArtifactIds.galleryImagePicker,
    sourcesJar = sourcesJar
)