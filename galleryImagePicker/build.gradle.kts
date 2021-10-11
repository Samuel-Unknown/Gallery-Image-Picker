import java.util.Properties
import java.io.FileReader

plugins {
    id(ANDROID_LIBRARY_PLUGIN)
    id(KOTLIN_ANDROID_PLUGIN)
    id(KOTLIN_PARCELIZE_PLUGIN)
    id(MAVEN_PUBLISH_PLUGIN)
    id(SIGNING_PLUGIN)
}

android {
    compileSdk = Versions.Sdk.compileSdk

    defaultConfig {
        minSdk = Versions.Sdk.minSdk
        targetSdk = Versions.Sdk.targetSdk
        testInstrumentationRunner = ANDROID_TEST_INSTRUMENTATION_RUNNER

        buildFeatures {
            viewBinding = true
        }

        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_11
            targetCompatibility = JavaVersion.VERSION_11
        }
    }
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
    implementation(Libraries.AndroidX.Lifecycle.runtimeKtx)

    // Material
    implementation(Libraries.Google.Android.material)
}

val sourcesJar by tasks.registering(Jar::class) {
    archiveClassifier.set("sources")
    from(android.sourceSets.getByName("main").java.srcDirs)
}

afterEvaluate {
    val propertiesFile = rootProject.file(Publishing.Properties.FileName)
    val properties = Properties()
    FileReader(propertiesFile).use { reader -> properties.load(reader) }

    publishing {
        publications {
            create<MavenPublication>("mavenSources") {
                groupId = Publishing.groupId
                artifactId = Publishing.artifactId
                version = Publishing.version

                from(components.getByName("release"))

                // NB: this is what makes the source code jar available in the published package
                artifact(sourcesJar)

                pom {
                    name.set(Publishing.POM.name)
                    description.set(Publishing.POM.description)
                    url.set(Publishing.POM.url)

                    licenses {
                        license {
                            name.set(Publishing.POM.License.name)
                            url.set(Publishing.POM.License.url)
                        }
                    }

                    developers {
                        developer {
                            id.set(Publishing.POM.Developer.id)
                            name.set(Publishing.POM.Developer.name)
                            email.set(Publishing.POM.Developer.email)
                        }
                    }

                    scm {
                        url.set(Publishing.POM.url)
                    }
                }
            }
        }

        repositories {
            // GitHub
            maven {
                name = Publishing.Repository.GitHub.name
                url = uri(Publishing.Repository.GitHub.url)

                credentials {
                    username = properties.getProperty(Publishing.Properties.GitHub.userId)
                    password = properties.getProperty(Publishing.Properties.GitHub.token)
                }
            }

            // Sonatype Stage
            maven {
                name = Publishing.Repository.SonatypeStage.name
                url = uri(Publishing.Repository.SonatypeStage.url)
                credentials {
                    username = properties.getProperty(Publishing.Properties.Sonatype.username)
                    password = properties.getProperty(Publishing.Properties.Sonatype.password)
                }
            }
        }
    }

    signing {
        useInMemoryPgpKeys(
            properties.getProperty(Publishing.Properties.Signing.KeyId),
            properties.getProperty(Publishing.Properties.Signing.Key),
            properties.getProperty(Publishing.Properties.Signing.Password)
        )

        sign(publishing.publications)
    }
}