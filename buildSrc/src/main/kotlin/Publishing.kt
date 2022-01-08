@file:Suppress("MayBeConstant")

object Publishing {
    val groupId = "io.github.samuel-unknown"
    val artifactId = "gallery-image-picker"
    val version = Versions.Library.versionName

    object ArtifactIds {
        val galleryImagePicker = "gallery-image-picker"
        val galleryImagePickerGlide = "gallery-image-picker-glide"
        val galleryImagePickerCoil = "gallery-image-picker-coil"
    }

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
        object SonatypeStage {
            val name = "SonatypeStaging"
            val url = "https://s01.oss.sonatype.org/service/local/staging/deploy/maven2"
        }
    }
}