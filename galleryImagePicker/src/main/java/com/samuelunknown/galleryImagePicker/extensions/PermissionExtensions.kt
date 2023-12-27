package com.samuelunknown.galleryImagePicker.extensions

import android.content.SharedPreferences
import android.content.pm.PackageManager
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

private const val PREF_KEY__WAS_PERMISSION_REQUESTED_RATIONALE = "PREF_KEY__WAS_PERMISSION_REQUESTED_RATIONALE"

private fun getSharedPreferencesPermissionKey(permission: String): String =
    "$PREF_KEY__WAS_PERMISSION_REQUESTED_RATIONALE $permission"

internal sealed class PermissionResult {
    data object Granted : PermissionResult()
    data class NotGranted(
        val permission: String,
        val isGrantingPermissionInSettingsRequired: Boolean
    ) : PermissionResult()
}

internal object SharedPreferencesCommitOperationException :
    Exception("SharedPreferences commit operation failed")

private suspend fun SharedPreferences.edit(
    operation: (SharedPreferences.Editor) -> Unit
) = suspendCoroutine { cont ->
    val editor = this.edit()
    operation(editor)
    if (editor.commit()) {
        cont.resume(Unit)
    } else {
        cont.resumeWithException(SharedPreferencesCommitOperationException)
    }
}

internal suspend fun Fragment.getWasPermissionRequestedRationale(
    permission: String
): Boolean = withContext(Dispatchers.IO) {
    val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
    val key = getSharedPreferencesPermissionKey(permission)
    return@withContext sharedPreferences.getBoolean(key, false)
}

internal suspend fun Fragment.setWasPermissionRequestedRationale(permission: String) = withContext(Dispatchers.IO) {
    val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
    val key = getSharedPreferencesPermissionKey(permission)
    sharedPreferences.edit {
        it.putBoolean(key, true)
    }
}

internal fun Fragment.isPermissionGranted(permission: String): Boolean {
    val result = ContextCompat.checkSelfPermission(requireContext(), permission)
    return result == PackageManager.PERMISSION_GRANTED
}

internal class PermissionLauncher private constructor(
    private val fragment: Fragment,
    private val permission: String,
    private val resultAction: (result: PermissionResult) -> Unit,
    private val requestPermissionLauncher: ActivityResultLauncher<String>
) {

    suspend fun request() {
        if (fragment.isPermissionGranted(permission)) {
            resultAction(PermissionResult.Granted)
            return
        }

        if (fragment.shouldShowRequestPermissionRationale(permission)) {
            requestPermissionLauncher.launch(permission)
            fragment.setWasPermissionRequestedRationale(permission)
        } else {
            if (fragment.getWasPermissionRequestedRationale(permission)) {
                resultAction(
                    PermissionResult.NotGranted(
                        permission = permission,
                        isGrantingPermissionInSettingsRequired = true
                    )
                )
            } else {
                requestPermissionLauncher.launch(permission)
            }
        }
    }

    companion object {
        fun init(
            fragment: Fragment,
            permission: String,
            coroutineScope: CoroutineScope,
            resultAction: (result: PermissionResult) -> Unit
        ): PermissionLauncher {

            val requestPermissionLauncher = fragment.registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                if (isGranted) {
                    resultAction(PermissionResult.Granted)
                } else {
                    coroutineScope.launch {
                        val isGrantingPermissionInSettingsRequired = fragment
                            .getWasPermissionRequestedRationale(permission)

                        resultAction(
                            PermissionResult.NotGranted(
                                permission = permission,
                                isGrantingPermissionInSettingsRequired = isGrantingPermissionInSettingsRequired
                            )
                        )
                    }
                }
            }

            return PermissionLauncher(
                fragment = fragment,
                permission = permission,
                resultAction = resultAction,
                requestPermissionLauncher = requestPermissionLauncher
            )
        }
    }
}
