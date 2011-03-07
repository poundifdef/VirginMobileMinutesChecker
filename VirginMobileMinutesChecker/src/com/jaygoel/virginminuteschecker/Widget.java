package com.jaygoel.virginminuteschecker;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;

import com.jaygoel.virginminuteschecker.UpdateService;

public class Widget extends AppWidgetProvider
{
	
	Context saved_context;
	AppWidgetManager saved_appWidgetManager;
	
    @Override
    public void onUpdate( Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds )
    {
    	context.startService(new Intent(context, UpdateService.class));
    }
}