package com.samuelunknown.library.presentation.ui.screen.gallery

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.samuelunknown.library.databinding.ActivityGalleryBinding
import com.samuelunknown.library.domain.model.ImagesResultDto
import com.samuelunknown.library.extensions.doOnApplyWindowInsetsListenerCompat
import com.samuelunknown.library.extensions.putImagesResultDto

class GalleryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGalleryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGalleryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        showGalleryFragment()
    }

    private fun showGalleryFragment() {
        val galleryFragment = GalleryFragment.init(
            onResultAction = { imagesResultDto -> finishWithResult(imagesResultDto) },
        )

        // In order to determine the screen height correctly inside the Gallery Fragment
        // (in cases with cutouts) we should show GalleryFragment after getting info about windowInsets
        WindowCompat.setDecorFitsSystemWindows(window, false)
        binding.root.doOnApplyWindowInsetsListenerCompat { _, _ ->
            galleryFragment.show(supportFragmentManager, galleryFragment.tag)
        }
    }

    private fun finishWithResult(result: ImagesResultDto) {
        setResult(
            Activity.RESULT_OK,
            Intent().putImagesResultDto(result)
        )
        finish()
    }
}