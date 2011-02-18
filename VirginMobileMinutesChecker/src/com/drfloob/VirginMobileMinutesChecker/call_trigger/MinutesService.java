package com.drfloob.VirginMobileMinutesChecker.call_trigger;

import android.app.Service;
import android.os.IBinder;
import android.os.Binder;
import android.content.Intent;
import android.widget.Toast;
import android.util.Log;
import android.content.SharedPreferences;

import com.jaygoel.virginminuteschecker.ViewMinutes;
import com.jaygoel.virginminuteschecker.WebsiteScraper;
import com.jaygoel.virginminuteschecker.IVMCScraper;
import com.jaygoel.virginminuteschecker.ReferenceScraper;
import com.jaygoel.virginminuteschecker.VMSettings;

import java.util.Timer;
import java.util.TimerTask;

public class MinutesService extends Service {

    public static final String ACTION= "com.drfloob.VirginMobileMinutesChecker.call_trigger.MinutesService.Action";
    public static final String ACTION_PARSE_TOAST= "parse_toast";
    public static final String ACTION_TOAST_LAST= "toast_last";
    public static final String ACTION_UPDATE= "update";
    public static final String ACTION_KILL_TOAST= "kill_toast";

    private static final String TAG= "DEBUG";

    private final Timer timer= new Timer("MinutesService");
    private ToastTask toastTask;
    private Toast theToast;

    @Override
    public IBinder onBind(Intent intent) {
	return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
	SharedPreferences settings = getSharedPreferences("settings", 0);
	if(!settings.getBoolean(VMSettings.OPTION_RUNSERVICE, true)) {
	    Log.d("DEBUG", "Settings said not to run");
	    return Service.START_NOT_STICKY;
	}

	Log.d(TAG, "in onStartCommand");
	String action= intent.getStringExtra(ACTION);

	if (action.equals(ACTION_PARSE_TOAST)) {
	    parseAndToast();
	} else if (action.equals(ACTION_TOAST_LAST)) {
	    toastLast();
	} else if (action.equals(ACTION_UPDATE)) {
	    killTimers();
	    update();
	} else if (action.equals(ACTION_KILL_TOAST)) {
	    killTimers();
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
	    Log.d(TAG, "Not Logged In ... asking for login credentials");

	    toast("Please login to update your remaining minutes");

	    Intent i = new Intent(this, ViewMinutes.class);
	    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	    startActivity(i);
	} else {
	    String html= WebsiteScraper.fetchScreen(username, password);
	    // Log.d(TAG, html);
	    IVMCScraper scraper= new ReferenceScraper();
	    
	    if (scraper.isValid(html)) {
		Log.d(TAG, "valid");
		minutes= scraper.getMinutesUsed(html);
		Log.d(TAG, minutes);

		SharedPreferences.Editor cedit= cache.edit();
		cedit.putString("minutes", minutes);
		cedit.commit();
	    } else {
		toast("There was a problem loading your Virgin Mobile page. Please login again.");
		Intent i = new Intent(this, ViewMinutes.class);
		startActivity(i);
	    }
	}
    }



    private void toast(String msg) {
	toast(msg, 10);
    }

    private void toast(String msg, int seconds) {
	theToast= Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT);
	toastTask= new ToastTask(theToast, seconds);
	timer.scheduleAtFixedRate(toastTask, 0, 1000);
    }

    private void killTimers() {
	Log.d("DEBUG", "killing timers");
	if (theToast != null)
	    theToast.cancel();
	if (toastTask != null)
	    toastTask.cancel();
	Log.d("DEBUG", "killing timers ... DONE");
   }

    private class ToastTask extends TimerTask {
	private Toast t;
	private int count;

	private int i= 0;

	ToastTask(Toast t, int count) {
	    this.t = t;
	    this.count = count;
	}

	@Override
	public void run() {
	    if (i > count) {
		cancel();
	    } else {
		++i;
		t.show();
	    }
	}

	@Override
	public boolean cancel() {
	    t.cancel();
	    return super.cancel();
	}
    }


    
}