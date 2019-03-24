package io.github.deweyreed.clipboardcleaner.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.widget.RemoteViews
import io.github.deweyreed.clipboardcleaner.ACTION_CLEAN
import io.github.deweyreed.clipboardcleaner.IntentActivity
import io.github.deweyreed.clipboardcleaner.R

/**
 * Implementation of App Widget functionality.
 */
class CleanWidget : AppWidgetProvider() {
    companion object {
        private fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            val views = RemoteViews(context.packageName, R.layout.widget_clean)
            val pi = PendingIntent.getActivity(
                context, 0,
                IntentActivity.activityIntent(context, ACTION_CLEAN), 0
            )
            views.setOnClickPendingIntent(R.id.viewWidgetClean, pi)
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }
}

