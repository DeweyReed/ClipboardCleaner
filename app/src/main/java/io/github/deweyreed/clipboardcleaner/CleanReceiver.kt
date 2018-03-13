package io.github.deweyreed.clipboardcleaner

import android.content.*
import android.widget.Toast


/**
 * Clean clipboard on receiving broadcast
 */
class CleanReceiver : BroadcastReceiver() {

    companion object {
        private const val PREFIX = "io.github.deweyreed.clipboardcleaner.action"
        const val ACTION_CLEAN = "$PREFIX.CLEAN"
        const val ACTION_CONTENT = "$PREFIX.CONTENT"

        fun cleanIntent(context: Context): Intent =
                Intent(context, CleanReceiver::class.java).setAction(ACTION_CLEAN)

        fun clean(context: Context) {
            context.sendBroadcast(cleanIntent(context))
        }

        fun contentIntent(context: Context): Intent =
                Intent(context, CleanReceiver::class.java).setAction(ACTION_CONTENT)

        fun content(context: Context) {
            context.sendBroadcast(contentIntent(context))
        }
    }

    private var toast: Toast? = null

    override fun onReceive(context: Context, intent: Intent) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        when (intent.action) {
            ACTION_CLEAN -> {
                if (clipboard.getClipContent(context).isNotEmpty()) {
                    clipboard.primaryClip = ClipData.newPlainText("text", "")
                    showToast(context, context.getString(R.string.toast_clipboard_cleaned))
                } else {
                    // to prevent loop during using a service
                    showToast(context, context.getString(R.string.toast_clipboard_is_empty))
                }
            }
            ACTION_CONTENT -> {
                showToast(context, clipboard.getClipContent(context))
            }
        }
    }

    private fun ClipboardManager.getClipContent(context: Context): String = primaryClip.let { clip ->
        if (clip != null && clip.itemCount > 0)
            clip.getItemAt(0).coerceToText(context).toString() else ""
    }

    private fun showToast(context: Context, text: String) {
        if (toast != null) {
            toast?.cancel()
            toast = null
        }
        toast = Toast.makeText(context, text, Toast.LENGTH_SHORT)
        toast?.show()
    }
}
