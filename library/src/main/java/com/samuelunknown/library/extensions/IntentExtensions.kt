package com.samuelunknown.library.extensions

import android.content.Intent
import com.samuelunknown.library.domain.model.GalleryConfigurationDto
import com.samuelunknown.library.domain.model.ImagesResultDto
import java.lang.Exception

private const val EXTRA_IMAGES_RESULT_DTO = "EXTRA_IMAGES_RESULT_DTO"
private const val EXTRA_CONFIGURATION_DTO = "EXTRA_GALLERY_CONFIGURATION_DTO"

fun Intent.getImagesResultDto(): ImagesResultDto? {
    return this.getParcelableExtra(EXTRA_IMAGES_RESULT_DTO)
}

fun Intent.putImagesResultDto(activityResultDto: ImagesResultDto): Intent {
    return this.putExtra(EXTRA_IMAGES_RESULT_DTO, activityResultDto)
}

fun Intent.getGalleryConfigurationDto(): GalleryConfigurationDto {
    return this.getParcelableExtra(EXTRA_CONFIGURATION_DTO)
        ?: throw Exception("${GalleryConfigurationDto::class.java} not found.")
}

fun Intent.putGalleryConfigurationDto(activityResultDto: GalleryConfigurationDto): Intent {
    return this.putExtra(EXTRA_CONFIGURATION_DTO, activityResultDto)
}