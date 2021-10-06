@file:Suppress("MayBeConstant")

object Publishing {
    val groupId = "com.samuelunknown"
    val artifactId = "gallery-image-picker"
    val version = Versions.Library.versionName

    object POM {
        val url = "https://github.com/samuel-unknown/gallery-image-picker"
        val name = "Gallery Image Picker"
        val description = "Android library for picking images"
    }

    object License {
        val url = "http://www.apache.org/licenses/LICENSE-2.0.txt"
        val name = "The Apache License, Version 2.0"
    }

    object Developer {
        val id = "samuel-unknown"
        val name = "Samuel Unknown"
        val email = "samuelunknown@gmail.com"
    }

    object Repository {
        val name = "githubPackages"
        val url = "https://maven.pkg.github.com/samuel-unknown/gallery-image-picker"

        object Properties {
            val userId = "USER_ID"
            val token = "TOKEN"

            object File {
                val name = "github.properties"
                val token = "TOKEN"
            }
        }
    }
}