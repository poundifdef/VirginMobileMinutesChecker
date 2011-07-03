/**
 * 
 */
package com.baker.vm.ui;

import android.app.Activity;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.baker.vm.PreferencesUtil;
import com.jaygoel.virginminuteschecker.R;

/**
 * @author baker
 *
 */
public final class SettingsActivity extends Activity 
{

    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        // Inbound call settings
        ((CheckBox) findViewById(R.id.settingsInboundCall)).
        	setChecked(PreferencesUtil.getInboundCall(this));
        ((CheckBox) findViewById(R.id.settingsInboundCall)).
        	setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
			{
				PreferencesUtil.setInboundCall(SettingsActivity.this, isChecked);
			}
		});


        // Outbound call settings
        ((CheckBox) findViewById(R.id.settingsOutboundCall)).
        	setChecked(PreferencesUtil.getOutboundCall(this));
        ((CheckBox) findViewById(R.id.settingsOutboundCall)).
        	setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
			{
				PreferencesUtil.setOutboundCall(SettingsActivity.this, isChecked);
			}
		});


        // app name settings
        ((CheckBox) findViewById(R.id.settingsAppName)).
        	setChecked(PreferencesUtil.getAppName(this));
        ((CheckBox) findViewById(R.id.settingsAppName)).
        	setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
			{
				PreferencesUtil.setAppName(SettingsActivity.this, isChecked);
			}
		});
    }
}
