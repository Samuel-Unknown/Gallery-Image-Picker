package com.samuelunknown.library.presentation.ui.screen.gallery

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.samuelunknown.library.R
import com.samuelunknown.library.domain.GetImagesUseCase
import com.samuelunknown.library.domain.GetImagesUseCaseImpl
import com.samuelunknown.library.extensions.putImagesResultDto
import com.samuelunknown.library.presentation.model.ImagesResultDto
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class GalleryActivity : AppCompatActivity() {
    private val getImagesUseCase: GetImagesUseCase by lazy {
        GetImagesUseCaseImpl(contentResolver)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery)

        getImages()
    }

    private fun getImages() {
        // it's just an example
        lifecycleScope.launch {
            delay(2000)
            try {
                val images = getImagesUseCase.execute()
                finishWithResult(ImagesResultDto.Success(images.subList(0, 10)))
            } catch (ex: Exception) {
                finishWithResult(ImagesResultDto.Error.Unknown(ex.localizedMessage))
            }
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