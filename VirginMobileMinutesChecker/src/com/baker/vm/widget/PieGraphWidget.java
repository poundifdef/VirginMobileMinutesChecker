/**
 *
 */
package com.baker.vm.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.widget.RemoteViews;

import com.baker.vm.PreferencesUtil;
import com.baker.vm.VMAccount;
import com.baker.vm.ui.MinutesPieGraphDrawable;
import com.baker.vm.ui.MultipleAccountsActivity;
import com.jaygoel.virginminuteschecker.R;

/**
 * @author baker
 *
 */
public final class PieGraphWidget extends AppWidgetProvider
{
	@Override
	public void onUpdate(final Context context, final AppWidgetManager appWidgetManager,
			final int[] appWidgetIds)
	{
		for (final int appWidgetId : appWidgetIds)
		{
			final RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_1x1	);

			views.setImageViewBitmap(R.id.widget_pie_container, createPieChart(context));

            views.setOnClickPendingIntent(R.id.widget_pie_container,
                PendingIntent.getActivity(context, 0, new Intent(context, MultipleAccountsActivity.class), 0));

			appWidgetManager.updateAppWidget(appWidgetId, views);
		}

		super.onUpdate(context, appWidgetManager, appWidgetIds);
	}

	private Bitmap createPieChart(final Context context)
	{
		final VMAccount cache = PreferencesUtil.getCachedAccount(context);
		final MinutesPieGraphDrawable graph = new MinutesPieGraphDrawable(context, cache);

		final Canvas c = new Canvas();
		final Bitmap b = Bitmap.createBitmap(72, 72, Bitmap.Config.ARGB_8888);
		c.setBitmap(b);
		graph.drawOnCanvas(c, new Rect(0, 0, 72, 72));
		return b;
	}

}
