/**
 * 
 */
package com.baker.vm.widget;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;

/**
 * @author baker
 *
 * This class is almost certainly implemented 100% wrong.  But it ain't broke...
 * 
 */
public final class WidgetUtil 
{
	public static void updateAllWidgets(final Context context)
	{
		AppWidgetManager mgr = AppWidgetManager.getInstance(context);
		
		int[] ids = mgr.getAppWidgetIds(new ComponentName(context, Simple2x1Widget.class));
		if (ids.length > 0)
		{
			new Simple2x1Widget().onUpdate(context, mgr, ids);
		}
		
		ids = mgr.getAppWidgetIds(new ComponentName(context, PieGraphWidget.class));
		if (ids.length > 0)
		{
			new PieGraphWidget().onUpdate(context, mgr, ids);
		}
	}
}
