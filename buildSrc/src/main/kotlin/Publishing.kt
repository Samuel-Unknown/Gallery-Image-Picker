@file:Suppress("MayBeConstant")

object Publishing {
    val groupId = "io.github.samuel-unknown"
    val artifactId = "gallery-image-picker"
    val version = Versions.Library.versionName

    object POM {
        val url = "https://github.com/samuel-unknown/gallery-image-picker"
        val name = "Gallery Image Picker"
        val description = "Android library for picking images"

        object License {
            val url = "http://www.apache.org/licenses/LICENSE-2.0.txt"
            val name = "The Apache License, Version 2.0"
        }

        object Developer {
            val id = "samuel-unknown"
            val name = "Samuel Unknown"
            val email = "samuelunknown@gmail.com"
        }
    }

    object Properties {
        val FileName = "publish.properties"

        object GitHub {
            val userId = "GITHUB_USER_ID"
            val token = "GITHUB_TOKEN"
        }

        object Sonatype {
            val username = "SONATYPE_USERNAME"
            val password = "SONATYPE_PASSWORD"
        }

        object Signing {
            val KeyId = "PGP_SIGNING_KEY_ID"
            val Key = "PGP_SIGNING_KEY"
            val Password = "PGP_SIGNING_PASSWORD"
        }
    }

    object Repository {
        object GitHub {
            val name = "GitHubPackages"
            val url = "https://maven.pkg.github.com/samuel-unknown/gallery-image-picker"
        }
        object SonatypeStage {
            val name = "SonatypeStaging"
            val url = "https://s01.oss.sonatype.org/service/local/staging/deploy/maven2"
        }
    }
}