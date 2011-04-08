package com.baker.vm.service;

import java.util.Timer;
import java.util.TimerTask;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.baker.vm.PreferencesUtil;
import com.baker.vm.ScraperUtil;
import com.baker.vm.UsernamePassword;
import com.baker.vm.VMAccount;
import com.jaygoel.virginminuteschecker.R;

/**
 * @author baker
 *
 */
public final class NotifyRemainingMinutes extends BroadcastReceiver
{

    private static final String TAG = "NotifyMinutesRemaining";

    @Override
    public void onReceive(final Context context, final Intent intent)
    {
        try
        {
            Bundle extras = intent.getExtras();

            if (extras == null)
            {
                return;
            }

            String state = extras.getString(TelephonyManager.EXTRA_STATE);

            if (TelephonyManager.EXTRA_STATE_RINGING.equals(state))
            {
                toastRemainingMinutes(context);
            }
            else if (TelephonyManager.EXTRA_STATE_OFFHOOK.equals(state))
            {
                toastRemainingMinutes(context);
            }
            else if (TelephonyManager.EXTRA_STATE_IDLE.equals(state))
            {
                // wait 1 minute for website to update then update cache
                new Timer().schedule(new TimerTask()
                {
                    @Override
                    public void run()
                    {
                        updateCache(context);
                    }
                }, 1000 * 60);
            }
        }
        catch (Exception ex)
        {
            Log.e(TAG, "Exception: " + ex.getMessage(), ex);
        }

    }

    private void toastRemainingMinutes(final Context context)
    {
        int used = PreferencesUtil.getCacheMinutesUsed(context);
        int total = PreferencesUtil.getCacheMinutesTotal(context);
        String message;

        if (used > total)
        {
            message = context.getString(R.string.minutes_over, (used - total));
        }
        else
        {
            message = context.getString(R.string.minutes_left, (total - used));
        }

        Toast.makeText(context,
            message,
            Toast.LENGTH_SHORT).show();
    }

    private void updateCache(final Context context)
    {
        String number = PreferencesUtil.getDefaultTelephoneNumber(context);
        String password =
            PreferencesUtil.getPassword(context, number);

        if (password != null && password.length() != 0)
        {
            VMAccount acct =
                ScraperUtil.scrape(new UsernamePassword(number, password));

            if (acct.isValid())
            {
                PreferencesUtil.setCache(context, acct.getMinutesUsed());
            }
            else
            {
                Log.e(TAG, "Invalid Account.  Authentication Issue?");

                // invalidate cache if we can't access
                PreferencesUtil.setCache(context, "");
            }
        }
        else
        {
            Log.w(TAG,
                  "No password information for this phone's number: " + number);
        }
    }

}
