@file:Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}
include(
    ":galleryImagePicker",
    ":galleryImagePickerGlide",
    ":galleryImagePickerCoil",
    ":sample"
)
rootProject.name = "Gallery Image Picker"