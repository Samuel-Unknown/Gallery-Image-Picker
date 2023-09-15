import com.samuelunknown.galleryImagePicker.Publishing

plugins {
    alias(libs.plugins.galleryImagePicker.library)
    alias(libs.plugins.galleryImagePicker.publish)
    alias(libs.plugins.kotlin.parcelize)
}

android {
    namespace = "com.samuelunknown.galleryImagePicker"
    buildFeatures.viewBinding = true
}

dependencies {
    implementation(libs.kotlin.stdlib)

    implementation(libs.kotlinx.coroutines.android)

    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.fragment)
    implementation(libs.androidx.preference)
    implementation(libs.androidx.window)
    implementation(libs.androidx.lifecycle.runtime)

    implementation(libs.google.android.material)
}

val jarSources by tasks.registering(Jar::class) {
    archiveClassifier.set("sources")
    from(android.sourceSets.getByName("main").java.srcDirs)
}

configure<PublishConventionPluginExtension> {
    artifactId.set(Publishing.ArtifactIds.galleryImagePicker)
    jar.set(jarSources)
}