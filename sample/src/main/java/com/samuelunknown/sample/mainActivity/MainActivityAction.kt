package com.samuelunknown.sample.mainActivity

import com.samuelunknown.galleryImagePicker.domain.model.GalleryConfigurationDto
import com.samuelunknown.galleryImagePicker.domain.model.ImagesResultDto

sealed class MainActivityAction {
    sealed class ViewAction : MainActivityAction() {
        data class OpenGalleryAction(val dto: GalleryConfigurationDto) : ViewAction()
    }

    sealed class VmAction : MainActivityAction() {
        object PrepareToOpenGalleryAction : VmAction()
        data class ChangeMimeTypeFilterAction(val mimeTypeFilter: MimeTypeFilter) : VmAction()
        data class ChangeResultAction(val result: ImagesResultDto) : VmAction()
        data class ChangeSpanCountAction(val spanCountText: String) : VmAction()
        data class ChangeSpacingSizeAction(val spacingSizeText: String) : VmAction()
        data class ChangeOpenLikeBottomSheetAction(val openLikeBottomSheet: Boolean) : VmAction()
        data class ChangePeekHeightAction(val peekHeightText: String) : VmAction()
        data class ChangeIsDarkModeEnabledAction(val  isEnabled: Boolean) : VmAction()
        data class ChangeIsCustomStyleEnabledAction(val isEnabled: Boolean) : VmAction()
        data class ChangeIsSingleSelectionAction(val isEnabled: Boolean) : VmAction()
    }
}