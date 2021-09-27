package com.samuelunknown.library.presentation.ui.screen.gallery

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import com.samuelunknown.library.extensions.getImagesResultDto
import com.samuelunknown.library.extensions.putGalleryConfigurationDto
import com.samuelunknown.library.domain.model.GalleryConfigurationDto
import com.samuelunknown.library.domain.model.ImagesResultDto

class ImagesResultContract : ActivityResultContract<GalleryConfigurationDto, ImagesResultDto>() {
    override fun createIntent(context: Context, galleryConfigurationDto: GalleryConfigurationDto) =
        Intent(context, GalleryActivity::class.java)
            .putGalleryConfigurationDto(galleryConfigurationDto)

    override fun parseResult(resultCode: Int, intent: Intent?): ImagesResultDto {
        if (resultCode != Activity.RESULT_OK) {
            return ImagesResultDto.Error.Canceled
        }

        return intent?.getImagesResultDto() ?: ImagesResultDto.Error.Unknown()
    }
}