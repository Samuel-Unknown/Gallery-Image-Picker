package com.samuelunknown.library.domain

import com.samuelunknown.library.presentation.model.ImageDto

interface GetImagesUseCase {
    suspend fun execute(mimeTypes: List<String> = listOf()): List<ImageDto>
}