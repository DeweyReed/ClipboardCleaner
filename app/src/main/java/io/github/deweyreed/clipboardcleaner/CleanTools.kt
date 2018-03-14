package io.github.deweyreed.clipboardcleaner

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.support.annotation.StringDef

/**
 * Created on 2018/3/14.
 */

private const val PREFIX = "io.github.deweyreed.clipboardcleaner.action"
const val ACTION_CLEAN = "$PREFIX.CLEAN"
const val ACTION_CONTENT = "$PREFIX.CONTENT"

@StringDef(ACTION_CLEAN, ACTION_CONTENT)
@Retention(AnnotationRetention.SOURCE)
annotation class CleanAction

fun Context.clean(@CleanAction action: String) {
    val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    when (action) {
        ACTION_CLEAN -> {
            if (clipboard.getClipContent(this).isNotEmpty()) {
                clipboard.primaryClip = ClipData.newPlainText("text", "")
                toast(R.string.toast_clipboard_cleaned)
            } else {
                // to prevent loop during using a service
                toast(R.string.toast_clipboard_is_empty)
            }
        }
        ACTION_CONTENT -> {
            toast(clipboard.getClipContent(this))
        }
    }
}

private fun ClipboardManager.getClipContent(context: Context): String = primaryClip.let { clip ->
    if (clip != null && clip.itemCount > 0)
        clip.getItemAt(0).coerceToText(context).toString() else ""
}