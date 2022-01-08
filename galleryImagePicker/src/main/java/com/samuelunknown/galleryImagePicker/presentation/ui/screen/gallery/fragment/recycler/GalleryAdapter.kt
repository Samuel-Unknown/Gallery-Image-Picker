package com.samuelunknown.galleryImagePicker.presentation.ui.screen.gallery.fragment.recycler

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.annotation.Px
import androidx.core.view.doOnAttach
import androidx.core.view.doOnLayout
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.samuelunknown.galleryImagePicker.R
import com.samuelunknown.galleryImagePicker.databinding.ItemImageGalleryBinding
import com.samuelunknown.galleryImagePicker.extensions.updateHeight
import com.samuelunknown.galleryImagePicker.extensions.updateWidth
import com.samuelunknown.galleryImagePicker.presentation.imageLoader.ImageLoader
import com.samuelunknown.galleryImagePicker.presentation.imageLoader.ImageLoaderFactory
import com.samuelunknown.galleryImagePicker.presentation.model.GalleryItem

internal class GalleryAdapter(
    context: Context,
    private val spanCount: Int,
    @Px private val spacingSize: Int,
    private val imageLoaderFactory: ImageLoaderFactory,
    private val changeSelectionAction: (GalleryItem.Image) -> Unit
) : ListAdapter<GalleryItem, GalleryAdapter.GalleryItemViewHolder>(GalleryItemDiffItemCallback()) {

    private val layoutInflater = LayoutInflater.from(context)

    @Deprecated("Use submitList", ReplaceWith("submitList(newItems)"))
    fun updateItems(newItems: List<GalleryItem>) {
        submitList(newItems)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryItemViewHolder {
        val binding = ItemImageGalleryBinding.inflate(layoutInflater, parent, false)

        fun setImageSize(@IdRes constraintSetId: Int) {
            val space = (spanCount + 1) * spacingSize
            val size = (parent.width - space) / spanCount

            with(binding) {
                root.getConstraintSet(constraintSetId)
                    .getConstraint(R.id.image)
                    .apply {
                        layout.mWidth = size
                        layout.mHeight = size
                    }

                image.updateWidth(size)
                image.updateHeight(size)
            }
        }

        setImageSize(R.id.start)
        setImageSize(R.id.end)

        return GalleryItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GalleryItemViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is GalleryItem.Image -> holder.bind(item)
        }
    }

    override fun onBindViewHolder(
        holder: GalleryItemViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads)
        }
    }

    override fun onViewRecycled(holder: GalleryItemViewHolder) {
        holder.clear()
    }

    inner class GalleryItemViewHolder(
        val binding: ItemImageGalleryBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        private val imageLoader: ImageLoader = imageLoaderFactory.create()

        fun bind(imageItem: GalleryItem.Image) {
            with(binding) {
                root.apply {
                    setOnClickListener {
                        val clickedItem = getItem(layoutPosition) as GalleryItem.Image
                        changeSelectionAction(clickedItem)
                    }
                    setOnLongClickListener {
                        val clickedItem = getItem(layoutPosition) as GalleryItem.Image
                        changeSelectionAction(clickedItem)
                        true
                    }

                    doOnAttach {
                        root.progress = if (imageItem.isSelected) 1f else 0f
                    }
                }

                image.doOnLayout {
                    // load in doOnLayout is better because imageView have
                    // the exact size and we can use it for optimizations
                    imageLoader.load(imageView = image, uri = imageItem.uri)
                }

                counter.text = imageItem.counterText
            }
        }

        fun clear() {
            with(binding) {
                imageLoader.cancel(image)
                root.setOnClickListener(null)
                root.setOnLongClickListener(null)
            }
        }
    }
}