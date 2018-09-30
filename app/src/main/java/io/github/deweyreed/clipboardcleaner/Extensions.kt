@file:Suppress("unused")

package io.github.deweyreed.clipboardcleaner

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat

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

fun Context.getSafeSharedPreference(): SharedPreferences = PreferenceManager.getDefaultSharedPreferences(safeContext())

fun Context.pendingActivityIntent(intent: Intent): PendingIntent {
    return PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
}

interface InputCallback {
    fun onInput(text: String)
}

@SuppressLint("InflateParams")
fun Context.requestKeywordInput(callback: InputCallback) {
    val builder = AlertDialog.Builder(this)
        .setTitle(R.string.setting_keyword_normal_title)
    val input = LayoutInflater.from(this).inflate(R.layout.dialog_input, null, false)
    builder.setView(input)
        .setPositiveButton(android.R.string.ok) { _, _ ->
            input.findViewById<EditText>(R.id.editDialogInput)?.text?.toString()
                .takeIf { it != null && it.isNotEmpty() }
                ?.let { callback.onInput(it) }
        }
        .setNegativeButton(android.R.string.cancel, null)
    builder.show()
}

//
// Shortcut
//

fun Context.createCleanShortcut() {
    createShortcut("clean",
            R.string.action_clipboard_clean_short, R.string.action_clipboard_clean,
            R.drawable.ic_shortcut_broom, ACTION_CLEAN)
}

fun Context.createContentShortcut() {
    createShortcut("content",
            R.string.action_clipboard_content_short, R.string.action_clipboard_content,
            R.drawable.ic_shortcut_clipboard, ACTION_CONTENT)
}

private fun Context.createShortcut(id: String,
                                   @StringRes shortLabelRes: Int, @StringRes longLabelRes: Int,
                                   @DrawableRes iconRes: Int, action: String) {
    if (ShortcutManagerCompat.isRequestPinShortcutSupported(this)) {
        ShortcutManagerCompat.requestPinShortcut(this,
                ShortcutInfoCompat.Builder(this, id)
                        .setShortLabel(getString(shortLabelRes))
                        .setLongLabel(getString(longLabelRes))
                        .setDisabledMessage(getString(R.string.shortcut_disabled))
                        .setIcon(IconCompat.createWithResource(this, iconRes))
                        .setIntent(IntentActivity.activityIntent(this, action))
                        .build(), PendingIntent.getBroadcast(this, 0,
                IntentActivity.activityIntent(this, ACTION_CONTENT), 0).intentSender)
        // Show current clipboard content after shortcut's created
    } else {
        toast(R.string.shortcut_no_permission)
    }
}