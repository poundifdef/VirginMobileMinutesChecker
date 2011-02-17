package com.drfloob.VirginMobileMinutesChecker.call_trigger;

import android.app.Service;
import android.os.IBinder;
import android.os.Binder;
import android.content.Intent;
import android.widget.Toast;
import android.util.Log;
import android.content.SharedPreferences;

import com.jaygoel.virginminuteschecker.WebsiteScraper;
import com.jaygoel.virginminuteschecker.IVMCScraper;
import com.jaygoel.virginminuteschecker.ReferenceScraper;

public class MinutesService extends Service {

    public static final String ACTION= "com.drfloob.VirginMobileMinutesChecker.call_trigger.MinutesService.Action";
    public static final String ACTION_PARSE_TOAST= "parse_toast";
    public static final String ACTION_TOAST_LAST= "toast_last";
    public static final String ACTION_UPDATE= "update";

    private static final String TAG= "DEBUG";
    private final IBinder binder= new MinutesBinder();

    public class MinutesBinder extends Binder {
	MinutesService getService() {
	    return MinutesService.this;
	}
    }

    public IBinder onBind(Intent intent) {
	return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
	Log.d(TAG, "in onStartCommand");
	String action= intent.getStringExtra(ACTION);

	if (action.equals(ACTION_PARSE_TOAST)) {
	    parseAndToast();
	} else if (action.equals(ACTION_TOAST_LAST)) {
	    toastLast();
	} else if (action.equals(ACTION_UPDATE)) {
	    update();
	} else {
	    Log.e(TAG, "Unknown action: "+action);
	}

	return Service.START_STICKY;
    }



    private void parseAndToast() {
	SharedPreferences settings = getSharedPreferences("loginInfo", 0);
	String username = settings.getString("username", "u");
	String password = settings.getString("password", "p");

	SharedPreferences cache = getSharedPreferences("cache", 0);

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

	toast("Minutes Used: " + minutes);
    }


    private void toastLast() {
	SharedPreferences cache = getSharedPreferences("cache", 0);
	String minutes= cache.getString("minutes", "Unknown");
	toast("Minutes Used: " + minutes);
    }


    private void update() {
	SharedPreferences settings = getSharedPreferences("loginInfo", 0);
	String username = settings.getString("username", "u");
	String password = settings.getString("password", "p");

	SharedPreferences cache = getSharedPreferences("cache", 0);

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

	toast("Minutes Used: " + minutes);
    }



    private void toast(String msg) {
	toast(msg, Toast.LENGTH_LONG);
    }

    private void toast(String msg, int length) {
	Toast.makeText(getApplicationContext(), msg, length).show();
    }


    
}