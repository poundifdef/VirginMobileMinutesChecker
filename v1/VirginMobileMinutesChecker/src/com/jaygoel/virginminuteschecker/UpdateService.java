package com.jaygoel.virginminuteschecker;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.RemoteViews;

public class UpdateService extends Service {
	
	private static final String TAG= "DEBUG";
	public static final String EVENT= "com.jaygoel.virginminuteschecker.call_trigger.UpdateService.Action";
	private Context saved_context;
    
    private final PhoneStateListener phoneStateListener = new PhoneStateListener(){
        @Override
        public void onCallStateChanged(int state, String incomingNumber)
        {
        		Log.d(TAG, "in oncallstatechanged");
                String callState = "UNKNOWN";
                
                switch(state)
                {
                        case TelephonyManager.CALL_STATE_IDLE:          callState = "IDLE"; break;
                        case TelephonyManager.CALL_STATE_RINGING:       callState = "Ringing (" + incomingNumber + ")"; break;
                        case TelephonyManager.CALL_STATE_OFFHOOK:       callState = "Offhook"; break;
                }
                
                if (callState=="IDLE")
                {
                	Log.d(TAG, "does this shit work?");
                	UpdateService.update(saved_context);
                }

                super.onCallStateChanged(state, incomingNumber);
        }
    };
	
	@Override
    public void onCreate() {
    	Log.d(TAG, "in create");
		saved_context = this;
        update(saved_context);
        TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        tm.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
    }

    public static void update(Context context) {
    	Log.d(TAG, "in update");
    	AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
    	
        RemoteViews remoteViews;
        ComponentName widget;

        remoteViews = new RemoteViews( context.getPackageName(), R.layout.widget );
        widget = new ComponentName( context, Widget.class );
        
    	SharedPreferences settings = context.getSharedPreferences("loginInfo", 0);
    	String username = settings.getString("username", "u");
    	String password = settings.getString("password", "p");

    	SharedPreferences cache = context.getSharedPreferences("cache", 0);

    	String minutes;
    	if (username.equals("u") || password.equals("p")) {
    	    minutes= "Not Logged In";
    	    Log.d(TAG, "Not Logged In");
    	} else {
    	    String html= WebsiteScraper.fetchScreen(username, password);
    	    Log.d(TAG, html);
    	    IVMCScraper scraper= new ReferenceScraper();
    	    
    	    if (scraper.isValid(html)) {
    		Log.d(TAG, "valid");
    		minutes= scraper.getMinutesUsed(html);
    		Log.d(TAG, minutes);

    		SharedPreferences.Editor cedit= cache.edit();
    		cedit.putString("minutes", minutes);
    		cedit.commit();
    	    } else {
    		minutes= "Problem Loading Page";
    	    }
    	}
        
        remoteViews.setTextViewText( R.id.widget_textview, "Minutes Used: " + minutes);
        appWidgetManager.updateAppWidget( widget, remoteViews );
    }
	@Override
	public IBinder onBind(Intent arg0) {
    	Log.d(TAG, "in onbind (shouldn't happen)");
		// TODO Auto-generated method stub
		return null;
	}
}