/**
 *
 */
package com.baker.vm.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import com.baker.vm.PreferencesUtil;
import com.baker.vm.service.NotifyRemainingMinutes;
import com.jaygoel.virginminuteschecker.R;

/**
 * @author baker
 *
 */
public final class Simple2x1Widget extends AppWidgetProvider
{
    public static final String UPDATE_ACTION = "updateWidget";
    public static final String START_THINKING = "startThinking";
    private static final String TAG = "NotifyMinutesRemaining";

    @Override
    public void onUpdate(final Context context,
        final AppWidgetManager appWidgetManager,
        final int[] appWidgetIds)
    {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        Log.i(TAG, "Updating Balance and Minutes!");

        updateTextFields(context, appWidgetManager, appWidgetIds, true);
    }

    @Override
    public void onReceive(final Context context, final Intent intent)
    {
        super.onReceive(context, intent);

        if (UPDATE_ACTION.equals(intent.getAction()))
        {
            updateTextFields(context, null, null, false);
        }
        else if (START_THINKING.equals(intent.getAction()))
        {
            RemoteViews views =
                new RemoteViews(context.getPackageName(), R.layout.widget_2x1);

            AppWidgetManager mgr = AppWidgetManager.getInstance(context);
            int[] appWidgetIds = mgr.getAppWidgetIds(new ComponentName(context, Simple2x1Widget.class));
            for (int i = 0, n = appWidgetIds.length; i < n; ++i)
            {
                views.setViewVisibility(R.id.widget_progress, View.VISIBLE);
                mgr.updateAppWidget(appWidgetIds[i], views);
            }
        }
    }

    private void updateTextFields(final Context context,
        AppWidgetManager mgr,
        int[] appWidgetIds,
        final boolean updateRefreshListener)
    {
        if (mgr == null)
        {
            mgr = AppWidgetManager.getInstance(context);
        }
        if (appWidgetIds == null)
        {
            appWidgetIds =
                mgr.getAppWidgetIds(new ComponentName(context, Simple2x1Widget.class));
        }
        RemoteViews views =
            new RemoteViews(context.getPackageName(), R.layout.widget_2x1);

        for (int i = 0, n = appWidgetIds.length; i < n; ++i)
        {
            int id = appWidgetIds[i];

            // update balance
            views.setTextViewText(R.id.widget_currentbalanceview,
                PreferencesUtil.getBalance(context));

            // update minutes used
            views.setTextViewText(R.id.widget_minutesusedview,
                PreferencesUtil.getMinutesUsed(context) + "");

            // update timestamp
            views.setTextViewText(R.id.widget_timestamp,
                context.getString(R.string.lastUpdated, " " + PreferencesUtil.getTimestamp(context)));

            if (updateRefreshListener)
            {
                // set button action
                views.setOnClickPendingIntent(R.id.widget_container,
                    PendingIntent.getBroadcast(context, 0, new Intent(context, NotifyRemainingMinutes.class), 0));
            }

            mgr.updateAppWidget(id, views);
        }
    }

}
