package io.github.deweyreed.clipboardcleaner

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED &&
            CleanService.getServiceStarted(context)
        ) {
            CleanService.start(context)
        }
    }
}
