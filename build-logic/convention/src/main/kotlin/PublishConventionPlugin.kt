import com.samuelunknown.galleryImagePicker.Publishing
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.getByType
import org.gradle.plugins.signing.SigningExtension
import java.io.FileReader
import java.util.Properties

interface PublishConventionPluginExtension {
    /**
     * Artifact name
     */
    val artifactId: Property<String>

    /**
     * Sources
     */
    val jar: Property<Any>
}

class PublishConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                // apply builtin Gradle plugins
                apply("org.gradle.maven-publish")
                apply("org.gradle.signing")
            }

            val extension = extensions.create<PublishConventionPluginExtension>("PublishConventionPluginExtension")
            extension.artifactId.convention("")
            extension.jar.convention("")

            val propertiesFile = rootProject.file(Publishing.Properties.FileName)
            if (propertiesFile.exists().not()) {
                println("File $propertiesFile doesn't exist")
                return
            }

            val properties = Properties()
            FileReader(propertiesFile).use { reader -> properties.load(reader) }

            afterEvaluate {
                extensions.configure<PublishingExtension> {
                    publications {
                        create<MavenPublication>("mavenSources") {
                            this.groupId = Publishing.groupId
                            this.artifactId = extension.artifactId.get()
                            this.version = Publishing.version

                            from(components.getByName("release"))

                            // NB: this is what makes the source code jar available in the published package
                            extension.jar.orNull?.let { artifact(it) }

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

                extensions.configure<SigningExtension> {
                    useInMemoryPgpKeys(
                        properties.getProperty(Publishing.Properties.Signing.KeyId),
                        properties.getProperty(Publishing.Properties.Signing.Key),
                        properties.getProperty(Publishing.Properties.Signing.Password)
                    )

                    sign(extensions.getByType<PublishingExtension>().publications)
                }
            }
        }
    }
}