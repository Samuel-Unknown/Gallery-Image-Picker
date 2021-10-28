package com.samuelunknown.sample.mainActivity

data class MainActivityState(
    val spanCount: Int = 4,
    val spacingSizeInPixels: Int = 8,
    val mimeTypeFilters: List<MimeTypeFilter> = listOf(
        MimeTypeFilter.Jpg(),
        MimeTypeFilter.Png(),
        MimeTypeFilter.Gif()
    ),
    val resultText: String = ""
) {
    val mimeTypes: List<String>
        get() = mimeTypeFilters
            .filter { it.isChecked }
            .map { it.name }
}

sealed class MimeTypeFilter(
    open val isChecked: Boolean,
    val name: String
) {
    data class Jpg(override val isChecked: Boolean = false) : MimeTypeFilter(isChecked, "image/jpeg")
    data class Png(override val isChecked: Boolean = false) : MimeTypeFilter(isChecked, "image/png")
    data class Gif(override val isChecked: Boolean = false) : MimeTypeFilter(isChecked, "image/gif")
}