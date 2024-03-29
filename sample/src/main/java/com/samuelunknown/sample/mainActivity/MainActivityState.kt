package com.samuelunknown.sample.mainActivity

data class MainActivityState(
    val spanCount: Int? = DEFAULT_SPAN_COUNT,
    val spanCountError: String? = null,
    val spacingSizeInPixels: Int? = DEFAULT_SPACING_SIZE_IN_PIXELS,
    val spacingSizeInPixelsError: String? = null,
    val mimeTypeFilters: List<MimeTypeFilter> = DEFAULT_MIME_TYPE_FILTERS,
    val openLikeBottomSheet: Boolean = DEFAULT_OPEN_LIKE_BOTTOM_SHEET,
    val peekHeightInPercents: Int? = DEFAULT_PEEK_HEIGHT,
    val peekHeightError: String? = null,
    val isDarkModeEnabled: Boolean = DEFAULT_IS_DARK_MODE_ENABLED,
    val isCustomStyleEnabled: Boolean = DEFAULT_IS_CUSTOM_STYLE_ENABLED,
    val isSingleSelectionEnabled: Boolean = DEFAULT_IS_SINGLE_SELECTION_ENABLED,
    val resultText: String = EMPTY_TEXT
) {
    val mimeTypes: List<String>
        get() = mimeTypeFilters
            .filter { it.isChecked }
            .map { it.name }

    companion object {
        const val DEFAULT_SPAN_COUNT = 4
        const val DEFAULT_SPACING_SIZE_IN_PIXELS = 8
        const val DEFAULT_OPEN_LIKE_BOTTOM_SHEET = true
        const val DEFAULT_PEEK_HEIGHT = 70
        const val DEFAULT_IS_DARK_MODE_ENABLED = false
        const val DEFAULT_IS_CUSTOM_STYLE_ENABLED = false
        const val DEFAULT_IS_SINGLE_SELECTION_ENABLED = false
        const val EMPTY_TEXT = ""
        const val EMPTY_VALUE_ERROR = "Error"
        val DEFAULT_MIME_TYPE_FILTERS = listOf(
            MimeTypeFilter.Jpg(isChecked = false),
            MimeTypeFilter.Png(isChecked = false),
            MimeTypeFilter.Gif(isChecked = false)
        )
    }
}

sealed class MimeTypeFilter(
    open val isChecked: Boolean,
    val name: String
) {
    data class Jpg(override val isChecked: Boolean) : MimeTypeFilter(isChecked, "image/jpeg")
    data class Png(override val isChecked: Boolean) : MimeTypeFilter(isChecked, "image/png")
    data class Gif(override val isChecked: Boolean) : MimeTypeFilter(isChecked, "image/gif")
}