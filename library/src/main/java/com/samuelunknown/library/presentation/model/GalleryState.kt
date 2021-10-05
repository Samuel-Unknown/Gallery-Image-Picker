package com.samuelunknown.library.presentation.model

import com.samuelunknown.library.domain.model.ImagesResultDto

sealed class GalleryState {
    object Init : GalleryState()
    data class Loaded(val items: List<GalleryItem>) : GalleryState()
    data class Picked(val result: ImagesResultDto.Success) : GalleryState()
    data class Error(val error: ImagesResultDto.Error) : GalleryState()
}
