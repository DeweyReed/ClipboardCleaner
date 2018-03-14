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
            if (intent?.action != null) {
                clean(intent.action)
            }
        } finally {
            finish()
        }
    }
}