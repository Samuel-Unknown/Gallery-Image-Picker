package com.samuelunknown.galleryImagePicker.presentation.ui.screen.gallery.fragment

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.samuelunknown.galleryImagePicker.domain.model.GalleryConfigurationDto
import com.samuelunknown.galleryImagePicker.domain.model.ImagesResultDto

class GalleryFragmentFactory(
    private val configurationDto: GalleryConfigurationDto,
    private val onResultAction: (ImagesResultDto) -> Unit = {}
) : FragmentFactory() {
    override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
        if (className == GalleryFragment::class.java.name) {
            return GalleryFragment.init(
                configurationDto = configurationDto,
                onResultAction = onResultAction
            )
        }
        return super.instantiate(classLoader, className)
    }
}