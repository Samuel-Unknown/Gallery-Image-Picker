package com.samuelunknown.library.domain

import android.content.ContentResolver
import android.net.Uri
import android.provider.MediaStore
import com.samuelunknown.library.domain.model.ImageDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GetImagesUseCaseImpl(
    private val contentResolver: ContentResolver
) : GetImagesUseCase {
    override suspend fun execute(mimeTypes: List<String>): List<ImageDto> {
        return withContext(Dispatchers.IO) {
            val images = mutableListOf<ImageDto>()
            val projection = arrayOf(
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.MIME_TYPE,
                MediaStore.Images.Media.DATE_MODIFIED
            )
            val sortOrder = "${MediaStore.Images.Media.DATE_MODIFIED} DESC"
            val selection = if (mimeTypes.isNotEmpty()) {
                mimeTypes.joinToString(
                    prefix = "${MediaStore.Images.Media.MIME_TYPE} IN (",
                    postfix = ")"
                ) { "?" }
            } else null
            val selectionArgs = mimeTypes.toTypedArray()

            contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                sortOrder
            )?.use { cursor ->
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                val displayNameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)

                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idColumn)
                    val displayName = cursor.getString(displayNameColumn)
                    val contentUri = Uri.withAppendedPath(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        id.toString()
                    )
                    images.add(ImageDto(uri = contentUri, name = displayName))
                }
            }

            images
        }
    }
}