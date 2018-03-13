package io.github.deweyreed.clipboardcleaner

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle

/**
 * Created on 2018/3/9.
 */

/**
 * Since shortcuts cannot send broadcast directly,
 * I use an activity without UI to handle the intent.
 */
class IntentActivity : Activity() {
    companion object {
        fun activityIntent(context: Context, action: String): Intent =
                Intent(context, IntentActivity::class.java).setAction(action)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        try {
            super.onCreate(savedInstanceState)
            if (intent != null && intent.action == CleanReceiver.ACTION_CONTENT) {
                CleanReceiver.content(this.applicationContext)
            } else {
                CleanReceiver.clean(this.applicationContext)
            }
        } finally {
            finish()
        }
    }
}