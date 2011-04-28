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
import com.baker.vm.widget.Simple2x1Widget;
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
        Log.e("MYOUTPUT", "onReceiverNotifyRemainingMinutes");
        try
        {
            Bundle extras = intent.getExtras();

            if (extras == null)
            {
                Log.i("MYOUTPUT", "screen scrape");

//              An attempt at making a "loading" icon/text appear when we are
//              attempting to get new information

//                Intent updateWidgetIntent = new Intent(context, Simple2x1Widget.class);
//                updateWidgetIntent.setAction(Simple2x1Widget.START_THINKING);
//                context.sendBroadcast(updateWidgetIntent);

                // Called from Widget
                updateCache(context);

                Intent updateWidgetIntent = new Intent(context, Simple2x1Widget.class);
                updateWidgetIntent.setAction(Simple2x1Widget.UPDATE_ACTION);
                context.sendBroadcast(updateWidgetIntent);

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
        int used = PreferencesUtil.getMinutesUsed(context);
        int total = PreferencesUtil.getCacheMinutesTotal(context);
        String minutes = PreferencesUtil.getMinutesString(context);

        if (minutes == null || minutes.length() == 0)
        {
            return;
        }
        else if (used < 0 || total < 0 )
        {
            // couldn't parse the minutes but we have a string value.
            // maybe they have unlimited and we don't want to show anything?
        }
        else
        {
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



    }

    private void updateCache(final Context context)
    {
        String number = PreferencesUtil.getDefaultTelephoneNumber(context);
        String password =
            PreferencesUtil.getPassword(context, number);

        if (password != null && password.length() != 0)
        {
            Log.i(TAG, "Checking Virgin Mobile's wesbite (" + number + ")");
            VMAccount acct =
                ScraperUtil.scrape(new UsernamePassword(number, password));

            if (acct.isValid())
            {
                Log.i(TAG, "Found: " + acct.getMinutesUsed() + " " + acct.getBalance());
                PreferencesUtil.setCache(context, acct);
            }
            else
            {
                Log.e(TAG, "Invalid Account.  Authentication Issue?");

                // invalidate cache if we can't access data
                PreferencesUtil.clearCache(context);
            }
        }
        else
        {
            Log.w(TAG,
                  "No password information for this phone's number: " + number);
        }
    }

}
