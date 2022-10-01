package com.samuelunknown.galleryImagePicker.presentation.ui.screen.gallery.fragment.recycler

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.Px
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
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
    private var imageSizeInPixels = 0
    private var ringSizeInPixels = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryItemViewHolder {
        val binding = ItemImageGalleryBinding.inflate(layoutInflater, parent, false)
        if (imageSizeInPixels == 0) {
            val space = (spanCount + 1) * spacingSize
            imageSizeInPixels = (parent.width - space) / spanCount
            ringSizeInPixels = (imageSizeInPixels * RING_SIZE_RATIO).toInt()
        }

        with(binding) {
            image.updateWidth(imageSizeInPixels)
            image.updateHeight(imageSizeInPixels)
            pickerRing.updateWidth(ringSizeInPixels)
            pickerRing.updateHeight(ringSizeInPixels)
            counter.updateWidth(ringSizeInPixels)
            counter.updateHeight(ringSizeInPixels)
        }

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
                }

                fun View.applyScale(startScale: Float, endScale: Float) = apply {
                    if (imageItem.isSelected) {
                        scaleX = endScale
                        scaleY = endScale
                    } else {
                        scaleX = startScale
                        scaleY = startScale
                    }
                }

                counter.applyScale(
                    startScale = GalleryItemAnimator.COUNTER_START_SCALE,
                    endScale = GalleryItemAnimator.COUNTER_END_SCALE
                )
                pickerRing.applyScale(
                    startScale = GalleryItemAnimator.RING_START_SCALE,
                    endScale = GalleryItemAnimator.RING_END_SCALE
                )
                image.applyScale(
                    startScale = GalleryItemAnimator.IMAGE_START_SCALE,
                    endScale = GalleryItemAnimator.IMAGE_END_SCALE
                )

                counter.text = imageItem.counterText
                imageLoader.load(imageView = image, uri = imageItem.uri, imageSizeInPixels)
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

    private companion object {
        const val RING_SIZE_RATIO = 0.3
    }
}