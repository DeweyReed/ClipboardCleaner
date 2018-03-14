package io.github.deweyreed.clipboardcleaner

import android.app.Service
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent

@Suppress("MemberVisibilityCanBePrivate")
class CleanService : Service(), ClipboardManager.OnPrimaryClipChangedListener {

    companion object {
        private const val PREF_SERVICE_STARTED = "pref_service_started"
        private const val PREF_SERVICE_OPTION = "pref_service_option"
        const val SERVICE_OPTION_CLEAN = 0
        const val SERVICE_OPTION_CONTENT = 1

        fun start(context: Context) {
            setServiceStarted(context, true)
            context.startService(Intent(context, CleanService::class.java))
        }

        fun stop(context: Context) {
            setServiceStarted(context, false)
            context.stopService(Intent(context, CleanService::class.java))
        }

        fun getServiceStarted(context: Context) = context.getSafeSharedPreference()
                .getBoolean(PREF_SERVICE_STARTED, false)

        fun setServiceStarted(context: Context, started: Boolean) =
                context.getSafeSharedPreference()
                        .edit().putBoolean(PREF_SERVICE_STARTED, started).apply()

        fun getServiceOption(context: Context): Int = context.getSafeSharedPreference()
                .getInt(PREF_SERVICE_OPTION, SERVICE_OPTION_CLEAN)

        fun setServiceOption(context: Context, option: Int) {
            context.getSafeSharedPreference().edit()
                    .putInt(PREF_SERVICE_OPTION, if (option in 0..1) option else 0).apply()
        }
    }

    override fun onBind(intent: Intent) = null

    override fun onCreate() {
        super.onCreate()
        (getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager)
                .addPrimaryClipChangedListener(this)
        toast(R.string.service_started)
    }

    override fun onPrimaryClipChanged() {
        val option = getServiceOption(this)
        if (option == SERVICE_OPTION_CLEAN) {
            clean(ACTION_CLEAN)
        } else if (option == SERVICE_OPTION_CONTENT) {
            clean(ACTION_CONTENT)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        (getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager)
                .removePrimaryClipChangedListener(this)
        toast(R.string.service_stopped)
    }
}
