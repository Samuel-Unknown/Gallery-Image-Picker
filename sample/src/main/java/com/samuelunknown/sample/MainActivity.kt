package com.samuelunknown.sample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.samuelunknown.library.domain.GetImagesUseCase
import com.samuelunknown.library.domain.GetImagesUseCaseImpl
import androidx.lifecycle.lifecycleScope
import com.samuelunknown.sample.databinding.ActivityMainBinding
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initButton()
    }

    private fun initButton() {
        val useCase: GetImagesUseCase = GetImagesUseCaseImpl(
            contentResolver = contentResolver
        )

        binding.getImages.setOnClickListener {
            lifecycleScope.launch {
                val images = useCase.execute()
                Log.d(TAG, "images: $images")
            }
        }
    }

    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }
}