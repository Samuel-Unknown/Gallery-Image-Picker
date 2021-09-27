package com.samuelunknown.library.presentation.ui.screen.gallery

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.samuelunknown.library.R
import com.samuelunknown.library.domain.model.ImagesResultDto
import com.samuelunknown.library.extensions.putImagesResultDto

class GalleryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery)
        showGalleryFragment()
    }

    private fun showGalleryFragment() {
        val galleryFragment = GalleryFragment.init(
            onAcceptAction = { imagesResultDto -> finishWithResult(imagesResultDto) },
            onCancelAction = { finishWithResult(ImagesResultDto.Error.Canceled) }
        )
        galleryFragment.show(supportFragmentManager, galleryFragment.tag)
    }

    private fun finishWithResult(result: ImagesResultDto) {
        setResult(
            Activity.RESULT_OK,
            Intent().putImagesResultDto(result)
        )
        finish()
    }
}