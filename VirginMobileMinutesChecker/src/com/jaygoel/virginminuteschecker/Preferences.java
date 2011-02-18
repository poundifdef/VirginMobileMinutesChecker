package com.jaygoel.virginminuteschecker;

import android.util.Log;
import android.content.Intent;
import com.drfloob.VirginMobileMinutesChecker.call_trigger.MinutesService;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;

public class Preferences extends PreferenceActivity {
    @Override
    protected void onCreate(Bundle savedInstanceBundle) {
	super.onCreate(savedInstanceBundle);
	addPreferencesFromResource(R.xml.preferences);
    }
}