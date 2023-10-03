@file:Suppress("MayBeConstant")
package com.samuelunknown.galleryImagePicker

object LibraryVersion {
    private val major = 1 // change when you make incompatible API changes
    private val minor = 6 // change when you add functionality in a backwards-compatible manner
    private val build = 2 // change when you make backwards-compatible bug fixes

    val versionCode = major * 10000 + minor * 100 + build
    val versionName = "${major}.${minor}.${build}"
}