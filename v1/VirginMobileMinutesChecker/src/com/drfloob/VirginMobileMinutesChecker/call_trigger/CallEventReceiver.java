package com.drfloob.VirginMobileMinutesChecker.call_trigger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.telephony.TelephonyManager;

public class CallEventReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

	Log.d("DEBUG", "CallEventReceiver Running");
	Log.d("DEBUG", intent.getAction());

	String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);

	Log.d("DEBUG", state);
	
	Intent i= new Intent(context, MinutesService.class);
	i.putExtra(MinutesService.EVENT, state);
	context.startService(i);
    }

}