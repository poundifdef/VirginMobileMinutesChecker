package com.drfloob.VirginMobileMinutesChecker.call_trigger;

import android.app.Service;
import android.os.IBinder;
import android.os.Binder;
import android.content.Intent;
import android.widget.Toast;
import android.util.Log;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;

import com.jaygoel.virginminuteschecker.ViewMinutes;
import com.jaygoel.virginminuteschecker.WebsiteScraper;
import com.jaygoel.virginminuteschecker.IVMCScraper;
import com.jaygoel.virginminuteschecker.ReferenceScraper;

import java.util.Timer;
import java.util.TimerTask;

public class MinutesService extends Service {

    public static final String EVENT= "com.drfloob.VirginMobileMinutesChecker.call_trigger.MinutesService.Action";

    private static final String TAG= "DEBUG";

    private final Timer timer= new Timer("MinutesService");
    private ToastTask toastTask;
    private Toast theToast;

    @Override
    public IBinder onBind(Intent intent) {
	return null;
    }

    /** Since we're always returning Service.START_NOT_STICKY, intent is guaranteed to not be null */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

	SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
	if(!settings.getBoolean("incomingCallPref", true)) {
	    Log.d("DEBUG", "Settings said not to run");
	    return Service.START_NOT_STICKY;
	}


	Log.d(TAG, "in onStartCommand");
	String event= intent.getStringExtra(EVENT);


	if (event.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
	    toastLast();
	} else if (event.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
	    killTimers();
	    update();
	} else if (event.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
	    if (settings.getBoolean("outgoingCallPref", true))
		toastLast();
	    else
		killTimers();
	} else {
	    Log.e(TAG, "Unknown event: "+event);
	}
	// parseAndToast();

	return Service.START_NOT_STICKY;
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
	    startViewMinutesActivity();
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
		startViewMinutesActivity();
	    }
	}
    }

    
    private void startViewMinutesActivity() {
	Intent i = new Intent(this, ViewMinutes.class);
	i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	startActivity(i);
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
