package com.samuelunknown.galleryImagePicker.domain.useCase.getFoldersUseCase

import android.content.ContentResolver
import android.provider.MediaStore
import com.samuelunknown.galleryImagePicker.domain.model.FolderDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal class GetFoldersUseCaseImpl(
    private val contentResolver: ContentResolver
) : GetFoldersUseCase {
    override suspend fun execute(): List<FolderDto> = withContext(Dispatchers.IO) {
        val projection = arrayOf(
            MediaStore.Images.Media.BUCKET_ID,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME
        )
        val sortOrder = "${MediaStore.Images.Media.BUCKET_DISPLAY_NAME} ASC"

        contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            sortOrder
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_ID)
            val bucketDisplayNameColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)

            List(cursor.count) {
                cursor.moveToNext()
                val folderId = cursor.getString(idColumn)
                val folderName = cursor.getString(bucketDisplayNameColumn)
                if (folderId.isNullOrEmpty() || folderName.isNullOrEmpty()) {
                    null
                } else {
                    FolderDto(id = folderId, name = folderName)
                }
            }.distinct().filterNotNull()

        } ?: emptyList()
    }
}