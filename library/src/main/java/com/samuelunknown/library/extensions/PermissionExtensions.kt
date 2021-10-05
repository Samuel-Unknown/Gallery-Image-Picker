package com.samuelunknown.library.extensions

import android.content.pm.PackageManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager

private const val PREF_KEY__WAS_PERMISSION_REQUESTED_RATIONALE = "PREF_KEY__WAS_PERMISSION_REQUESTED_RATIONALE"

private fun getSharedPreferencesPermissionKey(permission: String): String =
    "$PREF_KEY__WAS_PERMISSION_REQUESTED_RATIONALE $permission"

internal sealed class PermissionResult {
    object Granted : PermissionResult()
    data class NotGranted(val isGrantingPermissionInSettingsRequired: Boolean) : PermissionResult()
}

internal fun Fragment.getWasPermissionRequestedRationale(permission: String): Boolean {
    val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
    val key = getSharedPreferencesPermissionKey(permission)
    return sharedPreferences.getBoolean(key, false)
}

internal fun Fragment.setWasPermissionRequestedRationale(permission: String) {
    val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
    val key = getSharedPreferencesPermissionKey(permission)
    sharedPreferences.edit().apply {
        putBoolean(key, true)
        apply()
    }
}

internal fun Fragment.requestPermission(
    permission: String,
    resultAction: (result: PermissionResult) -> Unit
) {
    if (isPermissionGranted(permission)) {
        resultAction(PermissionResult.Granted)
        return
    }

    val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            resultAction(PermissionResult.Granted)
        } else {
            resultAction(
                PermissionResult.NotGranted(
                    isGrantingPermissionInSettingsRequired = getWasPermissionRequestedRationale(permission)
                )
            )
        }
    }

    if (shouldShowRequestPermissionRationale(permission)) {
        requestPermissionLauncher.launch(permission)
        setWasPermissionRequestedRationale(permission)
    } else {
        if (getWasPermissionRequestedRationale(permission)) {
            resultAction(PermissionResult.NotGranted(isGrantingPermissionInSettingsRequired = true))
        } else {
            requestPermissionLauncher.launch(permission)
        }
    }
}

internal fun Fragment.isPermissionGranted(permission: String): Boolean {
    val result = ContextCompat.checkSelfPermission(requireContext(), permission)
    return result == PackageManager.PERMISSION_GRANTED
}