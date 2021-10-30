package com.samuelunknown.galleryImagePicker.presentation.ui.screen.gallery

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.samuelunknown.galleryImagePicker.databinding.ActivityGalleryBinding
import com.samuelunknown.galleryImagePicker.domain.model.ImagesResultDto
import com.samuelunknown.galleryImagePicker.extensions.getGalleryConfigurationDto
import com.samuelunknown.galleryImagePicker.extensions.putImagesResultDto
import com.samuelunknown.galleryImagePicker.presentation.ui.screen.gallery.fragment.GalleryFragment
import com.samuelunknown.galleryImagePicker.presentation.ui.screen.gallery.fragment.GalleryFragmentFactory

internal class GalleryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGalleryBinding

    private val onResultAction: (ImagesResultDto) -> Unit = { result ->
        setResult(
            Activity.RESULT_OK,
            Intent().putImagesResultDto(result)
        )
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        supportFragmentManager.fragmentFactory = GalleryFragmentFactory(
            configurationDto = intent.getGalleryConfigurationDto(),
            onResultAction = onResultAction
        )
        super.onCreate(savedInstanceState)

        binding = ActivityGalleryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        if (savedInstanceState == null) {
            showGalleryFragment()
        }
    }

    private fun showGalleryFragment() {
        val galleryFragment = GalleryFragment.init(
            configurationDto = intent.getGalleryConfigurationDto(),
            onResultAction = onResultAction
        )
        galleryFragment.show(supportFragmentManager, GalleryFragment.TAG)
    }
}