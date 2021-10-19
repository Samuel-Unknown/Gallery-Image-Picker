package com.samuelunknown.galleryImagePicker.presentation.resultContract

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import com.samuelunknown.galleryImagePicker.extensions.getImagesResultDto
import com.samuelunknown.galleryImagePicker.extensions.putGalleryConfigurationDto
import com.samuelunknown.galleryImagePicker.domain.model.GalleryConfigurationDto
import com.samuelunknown.galleryImagePicker.domain.model.ImagesResultDto
import com.samuelunknown.galleryImagePicker.presentation.ui.screen.gallery.GalleryActivity

class ImagesResultContract : ActivityResultContract<GalleryConfigurationDto, ImagesResultDto>() {
    override fun createIntent(context: Context, galleryConfigurationDto: GalleryConfigurationDto) =
        Intent(context, GalleryActivity::class.java)
            .putGalleryConfigurationDto(galleryConfigurationDto)

    override fun parseResult(resultCode: Int, intent: Intent?): ImagesResultDto {
        if (resultCode != Activity.RESULT_OK) {
            return ImagesResultDto.Error.Unknown()
        }

        return intent?.getImagesResultDto() ?: ImagesResultDto.Error.Unknown()
    }
}