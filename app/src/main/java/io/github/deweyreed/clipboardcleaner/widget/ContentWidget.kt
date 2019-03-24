package io.github.deweyreed.clipboardcleaner.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.widget.RemoteViews
import io.github.deweyreed.clipboardcleaner.ACTION_CONTENT
import io.github.deweyreed.clipboardcleaner.IntentActivity
import io.github.deweyreed.clipboardcleaner.R

/**
 * Implementation of App Widget functionality.
 */
class ContentWidget : AppWidgetProvider() {
    companion object {
        private fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            val views = RemoteViews(context.packageName, R.layout.widget_content)
            val pi = PendingIntent.getActivity(
                context, 0,
                IntentActivity.activityIntent(context, ACTION_CONTENT), 0
            )
            views.setOnClickPendingIntent(R.id.viewWidgetContent, pi)
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

