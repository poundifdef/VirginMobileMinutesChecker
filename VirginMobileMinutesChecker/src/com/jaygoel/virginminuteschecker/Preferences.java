package com.jaygoel.virginminuteschecker;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class Preferences extends PreferenceActivity {
    @Override
    protected void onCreate(Bundle savedInstanceBundle) {
	super.onCreate(savedInstanceBundle);
	addPreferencesFromResource(R.xml.preferences);
    }
}