package io.github.deweyreed.clipboardcleaner

import android.app.Application
import android.content.Context
import android.os.Build
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatDelegate

class ClipboardCleaner : Application() {

    private var view: View? = null

    override fun onCreate() {
        super.onCreate()

        app = this

        AppCompatDelegate.setDefaultNightMode(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            } else {
                AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY
            }
        )
    }

    fun showSystemAlertWindow(windowContext: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && view == null) {
            view = View(this)
            (windowContext.getSystemService(WINDOW_SERVICE) as WindowManager).addView(
                view,
                WindowManager.LayoutParams().apply {
                    width = 1
                    height = 1
                    flags = flags or
                        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                        WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM
                }
            )
        }
    }

    fun dismissSystemAlertWindow(windowContext: Context) {
        if (view != null) {
            (windowContext.getSystemService(WINDOW_SERVICE) as WindowManager).removeView(view)
            view = null
        }
    }

    companion object {
        lateinit var app: ClipboardCleaner
    }
}

fun withSystemAlertWindow(windowContext: Context, f: () -> Unit) {
    ClipboardCleaner.app.run {
        showSystemAlertWindow(windowContext)
        f.invoke()
        dismissSystemAlertWindow(windowContext)
    }
}
