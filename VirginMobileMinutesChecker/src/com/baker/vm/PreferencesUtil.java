package com.baker.vm;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.telephony.TelephonyManager;
import android.text.format.DateFormat;
import android.util.Log;

import com.baker.vm.ui.MultipleAccountsActivity;

public final class PreferencesUtil
{
    private PreferencesUtil()
    {

    }

    /** Keys for different preferences. */
    public static final String AUTH_PREFS = "loginInfo";
    public static final String CACHE_PREFS = "cache";

    /** Keys in cache preferences. */
    public static final String CACHE_TS = "cache_timestamp";
    public static final String CACHE_AS_STRING = "cache_as_string";
    public static final String CACHE_MINUTES_USED = "cache_minutes_used";
    public static final String CACHE_MINUTES_TOTAL = "cache_minutes_total";

    public static final String CACHE_BALANCE = "cache_balance";

    /** Keys in auth preferences. */
    public static final String USER_PREFIX = "USER";
    public static final String PASS_PREFIX = "PASS";

    private static final Pattern MINUTES_PAT = Pattern.compile("(\\d+)\\s*/\\s*(\\d+)");
    private static final String TAG = "PreferencesUtil";

    public static SharedPreferences get(final Context context)
    {
        return context.getSharedPreferences(AUTH_PREFS, 0);
    }

    public static SharedPreferences getCache(final Context activity)
    {
        return activity.getSharedPreferences(CACHE_PREFS, 0);
    }

    public static String getBalance(final Context context)
    {
        return getCache(context).getString(CACHE_BALANCE, "");
    }

    public static String getMinutesString(final Context activity)
    {
        return getCache(activity).getString(CACHE_AS_STRING, "");
    }

    public static int getMinutesUsed(final Context activity)
    {
        return getCache(activity).getInt(CACHE_MINUTES_USED, -1);
    }

    public static int getCacheMinutesTotal(final Context activity)
    {
        return getCache(activity).getInt(CACHE_MINUTES_TOTAL, -1);
    }

    public static void clearCache(final Context context)
    {
        SharedPreferences cache = getCache(context);

        Editor editor = cache.edit();

        editor.putString(CACHE_AS_STRING, "");
        editor.putInt(CACHE_MINUTES_USED, -1);
        editor.putInt(CACHE_MINUTES_TOTAL, -1);

        editor.putString(CACHE_BALANCE, "");
    }

    public static void setCache(final Context activity, final VMAccount account)
    {
        int used = -1;
        int total = -1;

        SharedPreferences cache = getCache(activity);

        Editor editor = cache.edit();

        // Handle minutes used
        final String minutes = account.getMinutesUsed();
        editor.putString(CACHE_AS_STRING, minutes);
        Matcher m = MINUTES_PAT.matcher(minutes);

        if (m.matches())
        {
            try
            {
                used = Integer.parseInt(m.group(1));
            }
            catch (NumberFormatException ex)
            {
                Log.e(TAG, "Failed to parse minutes used as a number: " + minutes);
            }
            try
            {
                total = Integer.parseInt(m.group(2));
            }
            catch (NumberFormatException ex)
            {
                Log.e(TAG, "Failed to parse minutes used as a number: " + minutes);
            }
        }

        editor.putInt(CACHE_MINUTES_USED, used);
        editor.putInt(CACHE_MINUTES_TOTAL, total);

        // Handle account balance
        editor.putString(CACHE_BALANCE, account.getBalance());

        editor.putLong(CACHE_TS, System.currentTimeMillis());


        editor.commit();
    }

    public static String getDefaultTelephoneNumber(final Context c)
    {
        final TelephonyManager tMgr =
            (TelephonyManager) c.getSystemService(Context.TELEPHONY_SERVICE);

        final String number = tMgr.getLine1Number();
        return number == null ? "" : number;
    }

    public static void setAuth(final Context a, final UsernamePassword auth)
    {
        final SharedPreferences prefs = get(a);

        final Editor editor = prefs.edit();
        editor.putString(getPrefUserKey(auth), auth.user);
        editor.putString(getPrefPassKey(auth), auth.pass);
        editor.commit();
    }

    public static void removeNumber(final Context a, final String number)
    {
        final SharedPreferences prefs = get(a);

        final Editor editor = prefs.edit();
        editor.remove(getPrefUserKey(number));
        editor.remove(getPrefPassKey(number));
        editor.commit();
    }

    public static String getPassword(final Context a, final String phoneNumber)
    {
        return get(a).getString(getPrefPassKey(phoneNumber), null);
    }

    public static boolean containsNumber(final Context a, final String number)
    {
        return get(a).contains(getPrefUserKey(number));
    }

    public static boolean isUserPref(final String key)
    {
        if (key.startsWith(USER_PREFIX))
        {
            return true;
        }
        return false;
    }

    public static boolean isPasswordPref(final String key)
    {
        if (key.startsWith(PASS_PREFIX))
        {
            return true;
        }
        return false;
    }

    private static String getPrefUserKey(final UsernamePassword acct)
    {
        return getPrefUserKey(acct.user);
    }

    protected static String getPrefUserKey(final String phoneNumber)
    {
        return getPrefKey(USER_PREFIX, phoneNumber);
    }

    private static String getPrefPassKey(final UsernamePassword acct)
    {
        return getPrefPassKey(acct.user);
    }

    private static String getPrefPassKey(final String phoneNumber)
    {
        return getPrefKey(PASS_PREFIX, phoneNumber);
    }

    private static String getPrefKey(final String prefix, final String phoneNumber)
    {
        return prefix + MultipleAccountsActivity.digits(phoneNumber);
    }

    public static CharSequence getTimestamp(final Context context)
    {
        return DateFormat.format("E hh:mm aa", getCache(context).getLong(CACHE_TS, 0));
    }

}
