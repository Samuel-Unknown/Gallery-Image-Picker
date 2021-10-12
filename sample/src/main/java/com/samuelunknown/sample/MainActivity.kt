package com.samuelunknown.sample

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.samuelunknown.galleryImagePicker.domain.model.GalleryConfigurationDto
import com.samuelunknown.galleryImagePicker.domain.model.ImagesResultDto
import com.samuelunknown.galleryImagePicker.presentation.ui.screen.gallery.ImagesResultContract
import com.samuelunknown.sample.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    @SuppressLint("SetTextI18n")
    private val getImagesLauncher = registerForActivityResult(ImagesResultContract()) { result: ImagesResultDto ->
        when (result) {
            is ImagesResultDto.Success -> {
                if (result.images.isEmpty()) {
                    binding.result.text = "There is no selected images"
                } else {
                    binding.result.text = result.images.joinToString(
                        prefix = "Images:\n\n",
                        separator = "\n\n"
                    )
                }
            }
            is ImagesResultDto.Error -> {
                binding.result.text = "Error: ${result.message}"
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initButton()
    }

    private fun initButton() {
        binding.getImages.setOnClickListener {
            getImagesLauncher.launch(GalleryConfigurationDto())
        }
    }
}