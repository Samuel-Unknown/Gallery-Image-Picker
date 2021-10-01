package com.samuelunknown.library.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

sealed class ImagesResultDto : Parcelable {
    sealed class Error(open val message: String) : ImagesResultDto() {
        @Parcelize
        object PermissionError : Error("Permission error")

        @Parcelize
        data class Unknown(val customMessage: String? = null) : Error(customMessage ?: "Unknown error")
    }

    @Parcelize
    data class Success(val images: List<ImageDto> = listOf()) : ImagesResultDto()
}