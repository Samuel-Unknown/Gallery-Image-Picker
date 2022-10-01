package com.samuelunknown.galleryImagePicker.presentation.ui.screen.gallery.fragment.recycler

import android.animation.*
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.OvershootInterpolator
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView
import com.samuelunknown.galleryImagePicker.presentation.model.GalleryItemPayload
import java.util.*

internal class GalleryItemAnimator : DefaultItemAnimator() {

    private val viewHoldersInProgress: WeakHashMap<RecyclerView.ViewHolder, AnimatorSet> =
        WeakHashMap()

    override fun canReuseUpdatedViewHolder(
        viewHolder: RecyclerView.ViewHolder,
        payloads: MutableList<Any>
    ): Boolean = payloads.filterIsInstance<GalleryItemPayload.SelectionPayload>().isNotEmpty()

    override fun recordPreLayoutInformation(
        state: RecyclerView.State,
        viewHolder: RecyclerView.ViewHolder,
        changeFlags: Int,
        payloads: MutableList<Any>
    ): ItemHolderInfo {
        if (changeFlags == FLAG_CHANGED) {
            payloads.filterIsInstance<GalleryItemPayload.SelectionPayload>()
                .firstOrNull()
                ?.let { selectionPayload ->
                    return GalleryItemHolderInfo(
                        if (selectionPayload.item.isSelected)
                            UpdateAction.Select(counterText = selectionPayload.item.counterText)
                        else
                            UpdateAction.Deselect(counterText = selectionPayload.item.counterText)
                    )
                }
        }

        return super.recordPreLayoutInformation(state, viewHolder, changeFlags, payloads)
    }

    override fun animateChange(
        oldHolder: RecyclerView.ViewHolder,
        newHolder: RecyclerView.ViewHolder,
        preInfo: ItemHolderInfo,
        postInfo: ItemHolderInfo
    ): Boolean {
        if (preInfo is GalleryItemHolderInfo) {
            cancelCurrentAnimationIfExists(newHolder)
            viewHoldersInProgress.remove(newHolder)

            val holder = newHolder as GalleryAdapter.GalleryItemViewHolder
            animateHolder(holder, preInfo.updateAction)

            return false
        }

        return super.animateChange(oldHolder, newHolder, preInfo, postInfo)
    }

    override fun endAnimation(item: RecyclerView.ViewHolder) {
        super.endAnimation(item)
        cancelCurrentAnimationIfExists(item)
        viewHoldersInProgress.remove(item)
    }

    override fun endAnimations() {
        super.endAnimations()

        // removing by iterator to avoid ConcurrentModificationException
        val iterator = viewHoldersInProgress.iterator()
        while (iterator.hasNext()) {
            cancelCurrentAnimationIfExists(iterator.next().key)
            iterator.remove()
        }
    }

    private fun createAnimators(
        view: View,
        updateAction: UpdateAction,
        startValue: Float,
        endValue: Float,
        interpolator: TimeInterpolator
    ): List<ObjectAnimator> {

        return when (updateAction) {
            is UpdateAction.Select -> {
                if (view.scaleX == endValue) {
                    emptyList()
                } else {
                    listOf(
                        ObjectAnimator.ofFloat(view, "scaleX", startValue, endValue)
                            .also { it.interpolator = interpolator },
                        ObjectAnimator.ofFloat(view, "scaleY", startValue, endValue)
                            .also { it.interpolator = interpolator }
                    )
                }
            }
            is UpdateAction.Deselect -> {
                listOf(
                    ObjectAnimator.ofFloat(view, "scaleX", endValue, startValue)
                        .also { it.interpolator = interpolator },
                    ObjectAnimator.ofFloat(view, "scaleY", endValue, startValue)
                        .also { it.interpolator = interpolator }
                )
            }
        }
    }

    private fun animateHolder(
        holder: GalleryAdapter.GalleryItemViewHolder,
        updateAction: UpdateAction
    ) {
        val animatorListener = object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                viewHoldersInProgress.remove(holder)
                dispatchAnimationFinished(holder)
            }
        }

        with(holder.binding) {
            if (updateAction is UpdateAction.Select) {
                counter.text = updateAction.counterText
            }

            val animatorsCollection = listOf(
                createAnimators(
                    view = image,
                    updateAction = updateAction,
                    startValue = IMAGE_START_SCALE,
                    endValue = IMAGE_END_SCALE,
                    interpolator = IMAGE_INTERPOLATOR
                ),
                createAnimators(
                    view = pickerRing,
                    updateAction = updateAction,
                    startValue = RING_START_SCALE,
                    endValue = RING_END_SCALE,
                    interpolator = DEFAULT_INTERPOLATOR
                ),
                createAnimators(
                    view = counter,
                    updateAction = updateAction,
                    startValue = COUNTER_START_SCALE,
                    endValue = COUNTER_END_SCALE,
                    interpolator = DEFAULT_INTERPOLATOR
                )
            ).flatten()

            val scaleAnimatorSet = AnimatorSet()
                .apply {
                    duration = ANIMATION_DURATION
                    addListener(animatorListener)
                    playTogether(animatorsCollection)
                }

            viewHoldersInProgress[holder] = scaleAnimatorSet
            scaleAnimatorSet.start()
        }
    }

    private fun cancelCurrentAnimationIfExists(item: RecyclerView.ViewHolder) {
        viewHoldersInProgress[item]?.cancel()
    }

    private data class GalleryItemHolderInfo(val updateAction: UpdateAction) : ItemHolderInfo()

    private sealed class UpdateAction(open val counterText: String) {
        data class Select(override val counterText: String) : UpdateAction(counterText)
        data class Deselect(override val counterText: String) : UpdateAction(counterText)
    }

    companion object {
        const val IMAGE_START_SCALE = 1f
        const val IMAGE_END_SCALE = 0.8f
        const val RING_START_SCALE = 1f
        const val RING_END_SCALE = 0f
        const val COUNTER_START_SCALE = 0f
        const val COUNTER_END_SCALE = 1f
        private const val ANIMATION_DURATION = 350L
        private val DEFAULT_INTERPOLATOR = AccelerateDecelerateInterpolator()
        private val IMAGE_INTERPOLATOR = OvershootInterpolator()
    }
}