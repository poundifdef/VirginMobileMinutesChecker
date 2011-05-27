package com.baker.vm.ui;

import android.app.Activity;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.widget.TextView;

import com.jaygoel.virginminuteschecker.R;

public final class AboutActivity extends Activity
{
    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);

        String version = "";
        try
        {
            version = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            version = getString(R.string.version, version);
        }
        catch (NameNotFoundException e)
        {
            Log.e(getClass().getSimpleName().toUpperCase(),
                "Failed to acquire version.");
        }

        ((TextView) findViewById(R.id.about_version)).setText(version);
        TextView footer = (TextView) findViewById(R.id.about_footer);
        footer.setLinksClickable(true);
        footer.setMovementMethod(LinkMovementMethod.getInstance());


    }
}
