package com.samuelunknown.galleryImagePicker.extensions

import android.content.Intent
import androidx.core.os.BundleCompat
import com.samuelunknown.galleryImagePicker.domain.model.GalleryConfigurationDto
import com.samuelunknown.galleryImagePicker.domain.model.ImagesResultDto
import java.lang.Exception

private const val EXTRA_IMAGES_RESULT_DTO = "EXTRA_IMAGES_RESULT_DTO"
private const val EXTRA_CONFIGURATION_DTO = "EXTRA_CONFIGURATION_DTO"

internal fun Intent.getImagesResultDto(): ImagesResultDto? {
    return this.getParcelableItem(EXTRA_IMAGES_RESULT_DTO)
}

internal fun Intent.putImagesResultDto(activityResultDto: ImagesResultDto): Intent {
    return this.putExtra(EXTRA_IMAGES_RESULT_DTO, activityResultDto)
}

internal fun Intent.getGalleryConfigurationDto(): GalleryConfigurationDto {
    return this.getParcelableItem(EXTRA_CONFIGURATION_DTO)
        ?: throw Exception("${GalleryConfigurationDto::class.java} not found.")
}

internal fun Intent.putGalleryConfigurationDto(activityResultDto: GalleryConfigurationDto): Intent {
    return this.putExtra(EXTRA_CONFIGURATION_DTO, activityResultDto)
}

internal inline fun <reified T> Intent.getParcelableItem(key: String): T? {
    return extras?.let { BundleCompat.getParcelable(it, key, T::class.java) }
}
