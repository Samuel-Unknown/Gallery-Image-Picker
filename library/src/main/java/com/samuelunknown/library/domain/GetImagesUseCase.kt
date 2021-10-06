package com.samuelunknown.library.domain

import com.samuelunknown.library.domain.model.ImageDto

internal interface GetImagesUseCase {
    suspend fun execute(mimeTypes: List<String> = listOf()): List<ImageDto>
}