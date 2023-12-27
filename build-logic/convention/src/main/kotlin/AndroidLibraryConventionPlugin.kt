import com.android.build.gradle.LibraryExtension
import com.samuelunknown.galleryImagePicker.TARGET_SDK
import com.samuelunknown.galleryImagePicker.configureKotlinAndroid
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class AndroidLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.library")
                apply("org.jetbrains.kotlin.android")
            }

            extensions.configure<LibraryExtension> {
                configureKotlinAndroid(this)
                defaultConfig.targetSdk = TARGET_SDK

                buildTypes.getByName("release") {
                    // For a library project, minifyEnabled false means that the final AAR will not be processed
                    // with R8 i.e no code optimizations or dead code removal will be performed.
                    // If an app project has minifyEnabled true, R8 will process app code, all external
                    // (Maven) libraries, and local library projects, and it will use rules specified
                    // in the application to do that.
                    isMinifyEnabled = false

                    proguardFiles(
                        getDefaultProguardFile("proguard-android-optimize.txt"),
                        "proguard-rules.pro"
                    )
                }
            }
        }
    }
}
