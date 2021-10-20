@file:Suppress("unused")

package io.github.deweyreed.clipboardcleaner

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.text.InputType
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import androidx.preference.PreferenceManager

/**
 * Created on 2018/3/8.
 */

fun Context.dip(value: Int): Int = (value * resources.displayMetrics.density).toInt()

fun Context.toast(@StringRes id: Int) = toast(getString(id))

fun Context.toast(s: String) = Toast.makeText(this, s, Toast.LENGTH_SHORT).show()

fun Context.longToast(@StringRes id: Int) = longToast(getString(id))

fun Context.longToast(s: String) = Toast.makeText(this, s, Toast.LENGTH_LONG).show()

fun isKitkatOrLater(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
fun isNOrLater(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
fun isOOrLater(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O

fun Context.safeContext(): Context =
    takeIf { isNOrLater() && !isDeviceProtectedStorage }?.let {
        ContextCompat.createDeviceProtectedStorageContext(it) ?: it
    } ?: this

fun Context.getSafeSharedPreference(): SharedPreferences =
    PreferenceManager.getDefaultSharedPreferences(safeContext())

fun Context.pendingActivityIntent(intent: Intent): PendingIntent {
    var flags = PendingIntent.FLAG_UPDATE_CURRENT
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        flags = flags or PendingIntent.FLAG_IMMUTABLE
    }
    return PendingIntent.getActivity(this, 0, intent, flags)
}

fun Context.requestInput(
    @StringRes titleRes: Int,
    inputType: Int = InputType.TYPE_CLASS_TEXT,
    callback: (String) -> Unit
) {
    val dialog = AlertDialog.Builder(this)
        .setTitle(titleRes)
        .setView(R.layout.dialog_input)
        .setPositiveButton(android.R.string.ok, null)
        .setNegativeButton(android.R.string.cancel, null)
        .show()

    val input = dialog.findViewById<EditText>(R.id.editDialogInput)!!
    input.inputType = inputType

    dialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(android.R.string.ok)) { _, _ ->
        input.text?.toString()
            .takeIf { it != null && it.isNotEmpty() }
            ?.let { callback.invoke(it) }
    }
}

//
// Shortcut
//

fun Context.createCleanShortcut() {
    createShortcut(
        "clean",
        R.string.action_clipboard_clean_short, R.string.action_clipboard_clean,
        R.drawable.ic_shortcut_broom, ACTION_CLEAN
    )
}

fun Context.createContentShortcut() {
    createShortcut(
        "content",
        R.string.action_clipboard_content_short, R.string.action_clipboard_content,
        R.drawable.ic_shortcut_clipboard, ACTION_CONTENT
    )
}

private fun Context.createShortcut(
    id: String,
    @StringRes shortLabelRes: Int, @StringRes longLabelRes: Int,
    @DrawableRes iconRes: Int, action: String
) {
    if (ShortcutManagerCompat.isRequestPinShortcutSupported(this)) {
        ShortcutManagerCompat.requestPinShortcut(
            this,
            ShortcutInfoCompat.Builder(this, id)
                .setShortLabel(getString(shortLabelRes))
                .setLongLabel(getString(longLabelRes))
                .setDisabledMessage(getString(R.string.shortcut_disabled))
                .setIcon(IconCompat.createWithResource(this, iconRes))
                .setIntent(IntentActivity.activityIntent(this, action))
                .build(),
            PendingIntent.getBroadcast(
                this, 0,
                IntentActivity.activityIntent(this, ACTION_CONTENT),
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    PendingIntent.FLAG_IMMUTABLE
                } else {
                    0
                }
            ).intentSender
        )
        // Show current clipboard content after shortcut's created
    } else {
        toast(R.string.shortcut_no_permission)
    }
}