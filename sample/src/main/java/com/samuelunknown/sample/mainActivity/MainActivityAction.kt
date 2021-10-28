package com.samuelunknown.sample.mainActivity

import com.samuelunknown.galleryImagePicker.domain.model.GalleryConfigurationDto
import com.samuelunknown.galleryImagePicker.domain.model.ImagesResultDto

sealed class MainActivityAction {
    sealed class ViewAction {
        data class OpenGalleryAction(val dto: GalleryConfigurationDto) : ViewAction()
    }

    sealed class VmAction {
        object PrepareToOpenGalleryAction : VmAction()
        data class ChangeMimeTypeFilterAction(val mimeTypeFilter: MimeTypeFilter) : VmAction()
        data class ChangeResultAction(val result: ImagesResultDto) : VmAction()
    }
}