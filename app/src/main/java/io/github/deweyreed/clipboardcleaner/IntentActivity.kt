package io.github.deweyreed.clipboardcleaner

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings

/**
 * Created on 2018/3/9.
 */

/**
 * Since shortcuts cannot send broadcast directly,
 * I use an activity without UI to handle the intent.
 */
class IntentActivity : Activity() {
    companion object {
        fun activityIntent(
            context: Context, @CleanAction action: String,
            newTask: Boolean = false
        ): Intent =
            Intent(context, IntentActivity::class.java).setAction(action)
                .apply {
                    if (newTask) addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        try {
            super.onCreate(savedInstanceState)
            when {
                intent?.action == ACTION_CLEAN -> {
                    withSystemAlertWindow(this) {
                        clean()
                    }
                }
                intent?.action == ACTION_CONTENT -> {
                    withSystemAlertWindow(this) {
                        content()
                    }
                }
                Settings.Secure.getString(contentResolver, "assistant").let {
                    it != null && ComponentName.unflattenFromString(it)?.packageName == packageName
                } -> {
                    when (assistantAction) {
                        ACTION_CLEAN -> {
                            withSystemAlertWindow(this) {
                                clean()
                            }
                        }
                        ACTION_CONTENT -> {
                            withSystemAlertWindow(this) {
                                content()
                            }
                        }
                    }
                }
            }
        } finally {
            finish()
        }
    }
}