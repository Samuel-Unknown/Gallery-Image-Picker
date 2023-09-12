import com.android.build.gradle.LibraryExtension
import org.gradle.api.JavaVersion

fun LibraryExtension.applyConfig(
    isViewBindingEnabled: Boolean
) = defaultConfig {
    compileSdk = Versions.Sdk.compileSdk
    minSdk = Versions.Sdk.minSdk
    targetSdk = Versions.Sdk.targetSdk
    testInstrumentationRunner = ANDROID_TEST_INSTRUMENTATION_RUNNER

    buildFeatures {
        viewBinding = isViewBindingEnabled
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}