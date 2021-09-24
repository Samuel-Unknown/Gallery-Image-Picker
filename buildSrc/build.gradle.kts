plugins {
    `kotlin-dsl`
}

repositories {
    google()
    mavenCentral()
}

dependencies {
    // Note: we can't use here `implementation(Libraries.gradlePlugin)` and hence,
    // after upgrading version also should be changed in `Versions.AndroidTools.gradle`
    implementation("com.android.tools.build:gradle:7.0.2")
}