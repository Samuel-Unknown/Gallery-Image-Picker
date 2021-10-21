package com.samuelunknown.galleryImagePicker.domain

import com.samuelunknown.galleryImagePicker.domain.model.ImageDto

internal interface GetImagesUseCase {
    suspend fun execute(mimeTypes: List<String>): List<ImageDto>
}