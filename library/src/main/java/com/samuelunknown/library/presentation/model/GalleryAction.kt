package com.samuelunknown.library.presentation.model

sealed class GalleryAction {
    object Accept: GalleryAction()
    object Cancel: GalleryAction()
}