import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.create
import org.gradle.plugins.signing.SigningExtension
import java.io.FileReader
import java.util.Properties

fun Project.publishing(
    artifactId: String,
    sourcesJar: Any
) = afterEvaluate {
    val propertiesFile = rootProject.file(Publishing.Properties.FileName)
    val properties = Properties()
    FileReader(propertiesFile).use { reader -> properties.load(reader) }

    publishing {
        publications {
            create<MavenPublication>("mavenSources") {
                this.groupId = Publishing.groupId
                this.artifactId = artifactId
                this.version = Publishing.version

                from(components.getByName("release"))

                // NB: this is what makes the source code jar available in the published package
                artifact(sourcesJar)

                pom {
                    name.set(Publishing.POM.name)
                    description.set(Publishing.POM.description)

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

    signing {
        useInMemoryPgpKeys(
            properties.getProperty(Publishing.Properties.Signing.KeyId),
            properties.getProperty(Publishing.Properties.Signing.Key),
            properties.getProperty(Publishing.Properties.Signing.Password)
        )

        sign(publishing.publications)
    }
}

val Project.publishing: PublishingExtension
    get() = (this as org.gradle.api.plugins.ExtensionAware)
        .extensions.getByName("publishing") as PublishingExtension

fun Project.publishing(configure: Action<PublishingExtension>): Unit =
    (this as org.gradle.api.plugins.ExtensionAware)
        .extensions.configure("publishing", configure)

val Project.signing: SigningExtension
    get() = (this as org.gradle.api.plugins.ExtensionAware)
        .extensions.getByName("signing") as SigningExtension

fun Project.signing(configure: Action<SigningExtension>): Unit =
    (this as org.gradle.api.plugins.ExtensionAware)
        .extensions.configure("signing", configure)

