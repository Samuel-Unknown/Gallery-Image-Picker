package com.samuelunknown.galleryImagePicker.domain.useCase.getImagesUseCase

import android.content.ContentResolver
import android.net.Uri
import android.provider.MediaStore
import com.samuelunknown.galleryImagePicker.domain.model.FolderDto
import com.samuelunknown.galleryImagePicker.domain.model.ImageDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal class GetImagesUseCaseImpl(
    private val contentResolver: ContentResolver
) : GetImagesUseCase {
    override suspend fun execute(
        mimeTypes: List<String>,
        folder: FolderDto?
    ): List<ImageDto> = withContext(Dispatchers.IO) {
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.MIME_TYPE,
            MediaStore.Images.Media.DATE_MODIFIED
        )
        val sortOrder = "${MediaStore.Images.Media.DATE_MODIFIED} DESC"

        val mimeTypeSelection = mimeTypes.joinToString(
            prefix = "${MediaStore.Images.Media.MIME_TYPE} IN (",
            postfix = ")"
        ) { "?" }
        val folderIdSelection = "${MediaStore.Images.Media.BUCKET_ID} IN (?)"

        val selection = when {
            mimeTypes.isNotEmpty() && folder == null -> mimeTypeSelection
            mimeTypes.isEmpty() && folder != null -> folderIdSelection
            mimeTypes.isNotEmpty() && folder != null -> "$mimeTypeSelection AND $folderIdSelection"
            else -> null
        }

        val selectionArgs = when {
            mimeTypes.isNotEmpty() && folder == null -> mimeTypes.toTypedArray()
            mimeTypes.isEmpty() && folder != null -> arrayOf(folder.id)
            mimeTypes.isNotEmpty() && folder != null -> {
                mimeTypes.toTypedArray() + arrayOf(folder.id)
            }
            else -> emptyArray()
        }

        contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val displayNameColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)

            List(cursor.count) {
                cursor.moveToNext()
                val id = cursor.getLong(idColumn)
                val displayName = cursor.getString(displayNameColumn)
                val contentUri = Uri.withAppendedPath(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    id.toString()
                )
                ImageDto(uri = contentUri, name = displayName)
            }
        } ?: emptyList()
    }
}