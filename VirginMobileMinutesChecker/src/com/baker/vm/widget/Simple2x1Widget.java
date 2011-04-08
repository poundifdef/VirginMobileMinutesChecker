/**
 *
 */
package com.baker.vm.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.widget.RemoteViews;

import com.jaygoel.virginminuteschecker.R;

/**
 * @author baker
 *
 */
public final class Simple2x1Widget extends AppWidgetProvider
{
    @Override
    public void onUpdate(final Context context, final AppWidgetManager appWidgetManager,
        final int[] appWidgetIds)
    {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        for (int i = 0, n = appWidgetIds.length; i < n; ++i)
        {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_2x1);
        }
        // update model
    }

}
