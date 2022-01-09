package com.samuelunknown.galleryImagePicker.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
internal data class FolderDto(
    val id: String,
    val name: String
) : Parcelable