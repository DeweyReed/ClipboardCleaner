package io.github.deweyreed.clipboardcleaner

import android.app.*
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.support.v4.app.NotificationCompat

@Suppress("MemberVisibilityCanBePrivate")
class CleanService : Service(), ClipboardManager.OnPrimaryClipChangedListener {

    companion object {
        private const val PREF_SERVICE_STARTED = "pref_service_started"
        private const val PREF_SERVICE_OPTION = "pref_service_option"
        const val SERVICE_OPTION_CLEAN = 0
        const val SERVICE_OPTION_CONTENT = 1

        private const val CHANNEL_ID = "CHANNEL_CLEAN"

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

        fun isServiceRunning(context: Context): Boolean {
            val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            // it will still return the caller's own services for Android O
            @Suppress("DEPRECATION")
            for (service in manager.getRunningServices(Integer.MAX_VALUE)) {
                if (CleanService::class.java.name == service.service.className) {
                    return true
                }
            }
            return false
        }

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

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        buildChannelIfNecessary()
        startForeground(1, buildNotification())
        return START_STICKY
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

    private fun buildChannelIfNecessary() {
        if (isOOrLater()) {
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if (manager.getNotificationChannel(CHANNEL_ID) == null) {
                manager.createNotificationChannel(NotificationChannel(CHANNEL_ID,
                        getString(R.string.service_channel_name),
                        NotificationManager.IMPORTANCE_DEFAULT))
            }
        }
    }

    private fun buildNotification(): Notification = NotificationCompat.Builder(this,
            CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_broom_black_24dp)
            .setContentTitle(getString(R.string.service_notif_title))
            .setContentIntent(pendingActivityIntent(Intent(this,
                    MainActivity::class.java)))
            .addAction(R.drawable.ic_shortcut_broom,
                    getString(R.string.action_clipboard_clean_short),
                    pendingActivityIntent(
                            IntentActivity.activityIntent(this, ACTION_CLEAN)))
            .addAction(R.drawable.ic_shortcut_clipboard,
                    getString(R.string.action_clipboard_content_short),
                    pendingActivityIntent(
                            IntentActivity.activityIntent(this, ACTION_CONTENT)))
            .build()
}
