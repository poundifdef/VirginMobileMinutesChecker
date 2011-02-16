package com.jaygoel.virginminuteschecker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.EditText;

public class MinutesChecker extends Activity {
	public static final String PREFS_NAME = "loginInfo";

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		final EditText edittext = (EditText) findViewById(R.id.username);

		try {
			TelephonyManager tMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
			String phoneNumber = tMgr.getLine1Number();

			SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
			String username = settings.getString("username", "u");

			if (!username.equals("u")) {
				edittext.setText(username);
			} else if (phoneNumber != null) {
				edittext.setText(phoneNumber);
			}

		} catch (Exception e) {
			edittext.setText(e.getMessage());
		}

	}

	public void Login(View view) {

		String username = ((EditText) findViewById(R.id.username)).getText()
		.toString();
		String password = ((EditText) findViewById(R.id.password)).getText()
		.toString();

		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();

		editor.putString("username", username);
		editor.putString("password", password);

		// Commit the edits!
		editor.commit();

		Intent i = new Intent(this, ViewMinutes.class);
		startActivity(i);

	}
}