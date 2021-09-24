package com.samuelunknown.sample

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.samuelunknown.library.presentation.model.GalleryConfigurationDto
import com.samuelunknown.library.presentation.model.ImagesResultDto
import com.samuelunknown.library.presentation.ui.screen.gallery.ImagesResultContract
import com.samuelunknown.sample.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val getImagesLauncher = registerForActivityResult(ImagesResultContract()) { result: ImagesResultDto? ->
        when (result) {
            is ImagesResultDto.Success -> {
                Log.d(TAG, "images: ${result.images}")
            }
            is ImagesResultDto.Error -> {
                Log.d(TAG, "error: ${result.message}")
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
            getImagesLauncher.launch(
                GalleryConfigurationDto()
            )
        }
    }

    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }
}