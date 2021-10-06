import java.util.Properties
import java.io.FileReader

plugins {
    id(MAVEN_PUBLISH_PLUGIN)
}

// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath(Libraries.AndroidTools.gradle)
        classpath(kotlin("gradle-plugin", version = Versions.Kotlin.stdLib))
        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }

    // Enable to use Experimental APIs
    project.tasks.withType(org.jetbrains.kotlin.gradle.tasks.KotlinCompile::class).all {
        kotlinOptions {
            jvmTarget = JavaVersion.VERSION_11.toString()
        }
    }
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                artifact("$buildDir/outputs/aar/library-release.aar")

                groupId = Publishing.groupId
                artifactId = Publishing.artifactId
                version = Publishing.version

                pom {
                    name.set(Publishing.POM.name)
                    description.set(Publishing.POM.description)
                    url.set(Publishing.POM.url)

                    licenses {
                        license {
                            name.set(Publishing.License.name)
                            url.set(Publishing.License.url)
                        }
                    }

                    developers {
                        developer {
                            id.set(Publishing.Developer.id)
                            name.set(Publishing.Developer.name)
                            email.set(Publishing.Developer.email)
                        }
                    }

                    scm {
                        url.set(Publishing.POM.url)
                    }
                }
            }
        }

        repositories {
            maven {
                name = Publishing.Repository.Properties.File.name
                url = uri(Publishing.Repository.url)

                credentials {
                    val propertiesFile = rootProject.file(Publishing.Repository.Properties.File.name)
                    val properties = Properties()
                    FileReader(propertiesFile).use { reader -> properties.load(reader) }

                    username = properties.getProperty(Publishing.Repository.Properties.userId)
                    password = properties.getProperty(Publishing.Repository.Properties.token)
                }
            }
        }
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}