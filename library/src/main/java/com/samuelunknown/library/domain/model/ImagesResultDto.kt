package com.samuelunknown.library.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

sealed class ImagesResultDto : Parcelable {
    sealed class Error(open val message: String) : ImagesResultDto() {
        @Parcelize
        data class PermissionError(
            val permission: String,
            val isGrantingPermissionInSettingsRequired: Boolean
        ) : Error("Permission not granted: $permission.\n\nIs granting permission in settings required: $isGrantingPermissionInSettingsRequired")

        @Parcelize
        data class Unknown(val customMessage: String? = null) : Error(customMessage ?: "Unknown error")
    }

    @Parcelize
    data class Success(val images: List<ImageDto> = listOf()) : ImagesResultDto()
}