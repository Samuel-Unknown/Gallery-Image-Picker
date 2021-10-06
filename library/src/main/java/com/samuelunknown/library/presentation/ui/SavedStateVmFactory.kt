package com.samuelunknown.library.presentation.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner

internal interface SavedStateVmAssistedFactory<VM : ViewModel> {
    fun create(handle: SavedStateHandle): VM
}

internal class SavedStateVmFactory<out VM : ViewModel>(
    private val vmFactory: SavedStateVmAssistedFactory<VM>,
    owner: SavedStateRegistryOwner,
    defaultArgs: Bundle? = null
) : AbstractSavedStateViewModelFactory(owner, defaultArgs) {
    @Suppress("UNCHECKED_CAST")
    override fun <VM : ViewModel> create(
        key: String,
        modelClass: Class<VM>,
        handle: SavedStateHandle
    ): VM {
        return vmFactory.create(handle) as VM
    }
}

internal inline fun <reified VM : ViewModel> AppCompatActivity.savedStateViewModel(
    noinline vmFactoryProducer: () -> SavedStateVmAssistedFactory<VM>,
): Lazy<VM> {
    return viewModels {
        SavedStateVmFactory(
            vmFactory = vmFactoryProducer(),
            owner = this
        )
    }
}

internal inline fun <reified VM : ViewModel> Fragment.savedStateViewModel(
    noinline vmFactoryProducer: () -> SavedStateVmAssistedFactory<VM>,
): Lazy<VM> {
    return viewModels {
        SavedStateVmFactory(
            vmFactory = vmFactoryProducer(),
            owner = this
        )
    }
}