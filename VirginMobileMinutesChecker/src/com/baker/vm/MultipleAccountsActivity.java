/**
 *
 */
package com.baker.vm;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup.LayoutParams;
import android.widget.TextView;

import com.jaygoel.virginminuteschecker.IVMCScraper;
import com.jaygoel.virginminuteschecker.R;
import com.jaygoel.virginminuteschecker.ReferenceScraper;
import com.jaygoel.virginminuteschecker.WebsiteScraper;

/**
 * @author baker
 *
 */
public final class MultipleAccountsActivity extends Activity
{

    private static final String TEXTVIEW = "textview";
    private static final String TABLE = "table";

    private static final int DEFAULT_ACCOUNT_ID = 0;
    private static final String TAG = "MultipleAccounts";

    private static String getUserKey(final int accountNumber)
    {
        return "Acct" + accountNumber + "PhoneNumber";
    }
    private static String getPasswordKey(final int accountNumber)
    {
        return "Acct" + accountNumber + "Password";
    }

    private final Hashtable<String, View> hash = new Hashtable<String, View>();

    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.multipleaccounts);

        SharedPreferences prefs = getPreferences(MODE_PRIVATE);

        UsernamePassword acct = new UsernamePassword(
            prefs.getString(getUserKey(DEFAULT_ACCOUNT_ID), null),
            prefs.getString(getPasswordKey(DEFAULT_ACCOUNT_ID), null));

        if (acct.user == null || acct.user.length() == 0)
        {
            // prompt for initial phone number / password
            acct = promptForAuthentication(DEFAULT_ACCOUNT_ID);
        }
        else if (acct.pass == null || acct.pass.length() == 0)
        {
            // prompt for initial password (fill in phone number?)
            acct = promptForAuthentication(acct.user, DEFAULT_ACCOUNT_ID);
        }
        else
        {
            initializeAppFromPreferences();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu)
    {
        return super.onCreateOptionsMenu(menu);
    }

    private UsernamePassword promptForAuthentication(final int accountIdNumber)
    {
        return promptForAuthentication(null, accountIdNumber);
    }

    private UsernamePassword promptForAuthentication(final String user,
        final int accountIdNumber)
    {
        final Dialog dialog = new Dialog(this);

        dialog.setTitle("Account information");
        dialog.setContentView(R.layout.account_dialog);

        if (user != null)
        {
            ((EditText) dialog.findViewById(R.id.phoneNumberInputView)).setText(user);
        }

        final UsernamePassword auth = new UsernamePassword(null, null);

        dialog.findViewById(R.id.signInButton).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                String phoneNumber = ((EditText) dialog.findViewById(R.id.phoneNumberInputView)).getText().toString();
                String pass = ((EditText) dialog.findViewById(R.id.passwordInputView)).getText().toString();

                updatePreferences(getPreferences(MODE_PRIVATE),
                    getUserKey(accountIdNumber),
                    getPasswordKey(accountIdNumber),
                    new UsernamePassword(phoneNumber, pass));

                initializeAppFromPreferences();

                dialog.dismiss();
            }
        });

        dialog.setOnCancelListener(new DialogInterface.OnCancelListener()
        {
            @Override
            public void onCancel(final DialogInterface dlg)
            {
                MultipleAccountsActivity.this.finish();
            }
        });

        dialog.show();

        return auth;
    }

    private void initializeAppFromPreferences()
    {
        SharedPreferences prefs = getPreferences(MODE_PRIVATE);

        UsernamePassword acct = new UsernamePassword(
            prefs.getString(getUserKey(DEFAULT_ACCOUNT_ID), null),
            prefs.getString(getPasswordKey(DEFAULT_ACCOUNT_ID), null));

        List<UsernamePassword> auth = new ArrayList<UsernamePassword>();
        auth.add(acct);

        String user = null;
        int i = 1;
        do
        {
            user = prefs.getString(getUserKey(i), null);

            if (user != null)
            {
                String pass = prefs.getString(getPasswordKey(i), null);
                auth.add(new UsernamePassword(user, pass));
            }

            ++i;

        } while (user != null);

        initLayout(auth);

        new AsyncTask<UsernamePassword, Integer, List<VMAccount>>()
        {
            @Override
            protected void onCancelled()
            {
                super.onCancelled();

                findViewById(R.id.progress).setVisibility(View.GONE);
            }

            @Override
            protected void onPreExecute()
            {
                super.onPreExecute();

                findViewById(R.id.progress).setVisibility(View.VISIBLE);
            }

            @Override
            protected void onPostExecute(final List<VMAccount> result)
            {
                super.onPostExecute(result);

                MultipleAccountsActivity.this.setLayout(result);
                findViewById(R.id.progress).setVisibility(View.GONE);
            }

            @Override
            protected void onProgressUpdate(final Integer... values)
            {
                super.onProgressUpdate(values);

                if (values != null && values.length > 0)
                {
                    ProgressBar bar = (ProgressBar) findViewById(R.id.progress);
                    bar.setProgress(values[0]);
                }
            }

            @Override
            protected List<VMAccount> doInBackground(final UsernamePassword... params)
            {
                ((ProgressBar) findViewById(R.id.progress)).setMax(100);

                IVMCScraper scraper= new ReferenceScraper();

                List<VMAccount> accts = new ArrayList<VMAccount>();
                int index = 0;
                for (UsernamePassword a : params)
                {
                    String html= WebsiteScraper.fetchScreen(a.user, a.pass);
                    Log.d(TAG, html);

                    if (scraper.isValid(html))
                    {
                        Log.d(TAG, "valid");
                        accts.add(new VMAccount(html, scraper));
                    }
                    else
                    {
                        Log.d(TAG, "invalid: " + a.toString());
                        accts.add(VMAccount.createInvalid(a.user));
                    }

                    publishProgress((int) ((index++ / (float) params.length) * 100));
                }
                return accts;
            }
        }.execute(auth.toArray(new UsernamePassword[0]));
    }

    private void initLayout(final List<UsernamePassword> auths)
    {
        hash.clear();
        LinearLayout v = (LinearLayout) findViewById(R.id.accountView);

        for (UsernamePassword auth : auths)
        {
            LinearLayout vert = new LinearLayout(getApplicationContext());
            vert.setOrientation(LinearLayout.VERTICAL);

            vert.addView(createTextView(auth.user));

            LinearLayout table = new LinearLayout(getApplicationContext());
            table.setOrientation(LinearLayout.VERTICAL);
            table.setPadding(20, 0, 0, 0);
            hash.put(createKey(auth.user, TABLE), table);

            addRow(table, R.string.currentBalance, "");
            addRow(table, R.string.minutesUsed, "");
            addRow(table, R.string.chargedOn, "");
            addRow(table, R.string.monthlyCharge, "");

            vert.addView(table);

            v.addView(vert);
        }

    }

    private void setLayout(final List<VMAccount> accounts)
    {
        int i = 0;
        for (VMAccount acct : accounts)
        {
            TextView phoneNumber =
                (TextView) hash.get(createKey(acct.getNumber(), TEXTVIEW));
            phoneNumber.setText(acct.getNumber());
            phoneNumber.setTextColor(getResources().getColor(R.color.white));

            LinearLayout table =
                (LinearLayout) hash.get(createKey(acct.getNumber(), TABLE));
            table.removeAllViews();

            if (acct.isValid())
            {
                addRow(table, R.string.currentBalance, acct.getBalance());
                addRow(table, R.string.minutesUsed, acct.getMinutesUsed());
                addRow(table, R.string.chargedOn, acct.getChargedOn());
                addRow(table, R.string.monthlyCharge, acct.getMonthlyCharge());
            }
            else
            {
                addRow(table, -1, getString(R.string.loginFail));
                Editor editor = getPreferences(MODE_PRIVATE).edit();
                editor.remove(getPasswordKey(i));
                editor.commit();
            }

            ++i;
        }
    }

    private void addRow(final LinearLayout table,
        final int labelResId,
        final String value)
    {
        TextView lbl = new TextView(this);
        if (labelResId != -1)
        {
            lbl.setText(labelResId);
        }
        else
        {
            lbl.setText("");
        }
        lbl.setTextColor(getResources().getColor(R.color.gray));
        lbl.setTextSize(12F);
        lbl.setMinimumWidth(175);

        TextView val = new TextView(this);
        val.setText(value);
        val.setTextColor(getResources().getColor(R.color.white));

        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.addView(lbl, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        row.addView(val, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1F));

        table.addView(row, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
    }
    private View createTextView(final String user)
    {
        TextView text = new TextView(this);
        text.setText(user);
        hash.put(createKey(user, TEXTVIEW), text);
        text.setTextColor(getResources().getColor(R.color.gray));

        return text;
    }

    private String createKey(final String user, final String type)
    {
        return digits(user) + "|" + type;
    }

    private String digits(final String user)
    {
        return user.replaceAll("\\D", "");
    }

    private void updatePreferences(final SharedPreferences prefs,
                                   final String iDefaultUserKey,
                                   final String iDefaultPasswordKey,
                                   final UsernamePassword acct)
    {
        Editor editor = prefs.edit();
        editor.putString(iDefaultUserKey, acct.user);
        editor.putString(iDefaultPasswordKey, acct.pass);
        editor.commit();
    }

    private static class UsernamePassword
    {
        private UsernamePassword(final String iUser, final String iPass)
        {
            user = iUser;
            pass = iPass;
        }

        private final String user;
        private final String pass;

        @Override
        public String toString()
        {
            return user + " (" + pass + ")";
        }
    }
}
