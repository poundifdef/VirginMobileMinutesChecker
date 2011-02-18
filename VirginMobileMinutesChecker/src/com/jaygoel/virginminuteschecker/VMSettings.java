package com.jaygoel.virginminuteschecker;

import android.app.Activity;
import android.os.Bundle;
import android.widget.CheckBox;
import android.content.SharedPreferences;
import android.view.View;
import android.view.View.OnClickListener;
import android.util.Log;
import android.content.Intent;
import android.content.Intent;
import com.drfloob.VirginMobileMinutesChecker.call_trigger.MinutesService;

public class VMSettings extends Activity {

    public static final String OPTION_RUNSERVICE = "incomingNotificationOption";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.settings);

	SharedPreferences settings = getSharedPreferences("settings", 0);
	final SharedPreferences.Editor editor = settings.edit();

	final CheckBox incomingNotificationOption= (CheckBox) findViewById(R.id.incoming_notification_option);
	if (settings.getBoolean("incomingNotificationOption", true)) {
	    incomingNotificationOption.setChecked(true);
	} else {
	    incomingNotificationOption.setChecked(false);
	}

	incomingNotificationOption.setOnClickListener(new OnClickListener() {
		public void onClick(View v) {
		    if (((CheckBox)v).isChecked()) {
			// checking
			editor.putBoolean("incomingNotificationOption", true);
			Log.d("DEBUG", "incomingNotificationOption: TRUE");
		    } else {
			editor.putBoolean("incomingNotificationOption", false);
			Log.d("DEBUG", "incomingNotificationOption: FALSE");
			// stop the service, if running
			VMSettings.this.getApplicationContext().stopService(new Intent(VMSettings.this, MinutesService.class));
		    }
		    editor.commit();
		}
	    });
    }

}