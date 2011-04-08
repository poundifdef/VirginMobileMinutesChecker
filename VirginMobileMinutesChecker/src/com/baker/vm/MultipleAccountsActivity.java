/**
 *
 */
package com.baker.vm;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup.LayoutParams;
import android.widget.TextView;

import com.jaygoel.virginminuteschecker.R;

/**
 * @author baker
 *
 */
public final class MultipleAccountsActivity extends Activity
{

    private static final String TEXTVIEW = "textview";
    private static final String TABLE = "table";
    private static final String LAYOUT = "linearlayout";

    private final List<UsernamePassword> model = new ArrayList<UsernamePassword>();
    private final Hashtable<String, View> hash = new Hashtable<String, View>();

    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.multipleaccounts);
    }

	@Override
	protected void onResume()
	{
		super.onResume();

        // get all stored phone numbers / passwords
		// (and layout views)
        updateModelFromPreferences();

        // if they have no phone numbers then pop up a new account dialog
        if (model.isEmpty())
        {
            // prompt for initial phone number / password
            showAddAccountDialog(getUsersTelephoneNumber(), null);
        }
        else
        {
            new FetchAccountTask(this).execute(model.toArray(new UsernamePassword[0]));
        }

	}

	@Override
    public boolean onCreateOptionsMenu(final Menu menu)
    {
        final MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.multipleaccounts_menu, menu);
        return true;

    }

    @Override
	public boolean onOptionsItemSelected(final MenuItem item)
	{
		switch (item.getItemId())
		{
			case R.id.menu_refreshall:
				// update model to match preferences
				updateModelFromPreferences();

				// fetch data
	            new FetchAccountTask(this).execute(model.toArray(new UsernamePassword[0]));
				return true;

			case R.id.menu_accountsignout:

				removeAllPasswordsFromPreferences();
				updateModelFromPreferences();

				for (final UsernamePassword auth : model)
				{
					updateLayout(auth);
				}

				return true;

			case R.id.menu_addaccount:

				final SharedPreferences pref = getPreferences(MODE_PRIVATE);

				String startingNumber = null;
				if (!pref.contains(getPrefUserKey(getUsersTelephoneNumber())))
				{
					startingNumber = getUsersTelephoneNumber();
				}
				showAddAccountDialog(startingNumber, null);

				return true;

			default:
				return super.onOptionsItemSelected(item);
		}

	}

	private void removeAllPasswordsFromPreferences()
	{
        final List<String> keys = new ArrayList<String>();
        final SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        final Map<String, ?> map = prefs.getAll();

        for (final String key : map.keySet())
        {
        	if (isPasswordPref(key))
        	{
        		keys.add(key);
        	}
        }

        final Editor editor = prefs.edit();
        for (final String key : keys)
        {
        	editor.remove(key);
        }
        editor.commit();
	}

	private String getUsersTelephoneNumber()
	{
    	final TelephonyManager tMgr =
    		(TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);

    	final String number = tMgr.getLine1Number();
		return number == null ? "" : number;
	}

	private void updateModelFromPreferences()
	{
        model.clear();
        final SharedPreferences prefs = getPreferences(MODE_PRIVATE);

        final List<String> phoneNumbers = new ArrayList<String>();
        final Map<String, ?> map = prefs.getAll();

        for (final String key : map.keySet())
        {
        	if (isUserPref(key))
        	{
        		phoneNumbers.add(map.get(key).toString());
        	}
        }

        for (final String phoneNumber : phoneNumbers)
        {
        	model.add(new UsernamePassword(phoneNumber,
        			prefs.getString(getPrefPassKey(phoneNumber), null)));
        }

        doInitialLayout();
	}

    private void showAddAccountDialog(final String user, final String password)
    {
        final Dialog dialog = new Dialog(this);

        dialog.setTitle(R.string.addAccountDialogTitle);
        dialog.setContentView(R.layout.account_dialog);

        if (user != null && user.length() != 0)
        {
            ((EditText) dialog.findViewById(R.id.phoneNumberInputView)).setText(user);
            dialog.findViewById(R.id.passwordInputView).requestFocus();
        }

        if (password != null && password.length() != 0)
        {
        	final EditText passwordView = (EditText) dialog.findViewById(R.id.passwordInputView);
            passwordView.setText(password);
            passwordView.selectAll();
        }

        dialog.findViewById(R.id.signInButton).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                final String phoneNumber = ((EditText) dialog.findViewById(R.id.phoneNumberInputView)).getText().toString();
                final String pass = ((EditText) dialog.findViewById(R.id.passwordInputView)).getText().toString();

                final UsernamePassword auth = new UsernamePassword(phoneNumber, pass);
                updatePreferences(auth);

                updateLayout(auth);

                new FetchAccountTask(MultipleAccountsActivity.this).execute(auth);

                dialog.dismiss();
            }
        });

        dialog.setOnCancelListener(new DialogInterface.OnCancelListener()
        {
            @Override
            public void onCancel(final DialogInterface dlg)
            {
            	updateModelFromPreferences();
            	if (model.isEmpty())
            	{
            		MultipleAccountsActivity.this.finish();
            	}
            }
        });

        dialog.show();
    }

    private void doInitialLayout()
    {
        final LinearLayout v = (LinearLayout) findViewById(R.id.accountView);

        // Remove views that are no longer in our model
        final List<String> deadNumbers = new ArrayList<String>();
        for (final String key : hash.keySet())
        {
        	final String number = getNumberFromHashKey(key);
        	if (!doesModelContainPhoneNumber(number))
        	{
        		final View removeMe = hash.get(getHashKey(number, LAYOUT));
        		if (removeMe != null)
        		{
        			v.removeView(removeMe);
        			deadNumbers.add(number);
        		}
        	}
        }

        // Remove keys from hash (after iterating to avoid ConcurrentMod Exception)
        for (final String number : deadNumbers)
        {
        	hash.remove(getHashKey(number, TABLE));
        	hash.remove(getHashKey(number, TEXTVIEW));
        	hash.remove(getHashKey(number, LAYOUT));
        }

        // Add new views that were not in the model when we laid things out last
        for (final UsernamePassword auth : model)
        {
        	// only add initial layout pieces when it's not already there
        	if (!hash.containsKey(getHashKey(auth.user, TEXTVIEW)))
        	{
                final LinearLayout vert = new LinearLayout(getApplicationContext());
                vert.setOrientation(LinearLayout.VERTICAL);
                vert.setPadding(0, 10, 0, 0);
                hash.put(getHashKey(auth.user, LAYOUT), vert);

                vert.addView(createTextView(auth.user));

                final LinearLayout table = new LinearLayout(getApplicationContext());
                table.setOrientation(LinearLayout.VERTICAL);
                hash.put(getHashKey(auth.user, TABLE), table);

                updateLayout(auth);

                vert.addView(table);

                v.addView(vert);
        	}
        }

    }

	public void updateLayout(final UsernamePassword auth)
    {
        final TextView phoneNumber =
            (TextView) hash.get(getHashKey(auth.user, TEXTVIEW));
        phoneNumber.setText(auth.user);
        phoneNumber.setTextColor(getResources().getColor(R.color.gray));

        final LinearLayout table =
            (LinearLayout) hash.get(getHashKey(auth.user, TABLE));
        table.removeAllViews();

        if (auth.pass == null || auth.pass.length() == 0)
        {
        	addRow(table, createSignInButton(auth));
        }
        else
        {
            table.setPadding(20, 0, 0, 0);

            addRow(table, R.string.currentBalance, "");
            addRow(table, R.string.minutesUsed, "");
            addRow(table, R.string.chargedOn, "");
            addRow(table, R.string.monthlyCharge, "");
        }
    }

    public void updateLayout(final List<VMAccount> accounts)
    {
        for (final VMAccount acct : accounts)
        {
        	updateLayout(acct);
        }
    }

    public void updateLayout(final VMAccount acct)
    {
        final TextView phoneNumber =
        	(TextView) hash.get(getHashKey(acct.getNumber(), TEXTVIEW));
        phoneNumber.setText(acct.getNumber());
        phoneNumber.setTextColor(getResources().getColor(R.color.white));

        final LinearLayout table =
        	(LinearLayout) hash.get(getHashKey(acct.getNumber(), TABLE));
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
        	addRow(table, createSignInButton(acct.getAuth()), getString(R.string.loginFail));
        }
    }

    private void addRow(final LinearLayout table,
    					final int labelResId,
    					final String value)
    {
        final TextView lbl = new TextView(this);
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

        final TextView val = new TextView(this);
        val.setText(value);
        val.setTextColor(getResources().getColor(R.color.white));

        final LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.addView(lbl, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        row.addView(val, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1F));

        table.addView(row, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
    }

    private void addRow(final LinearLayout table,
    					final Button button)
    {
    	button.setPadding(20, 5, 20, 5);
        table.addView(button, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
    }

    private void addRow(final LinearLayout table,
    					final Button button,
    					final String message)
    {
        final TextView lbl = new TextView(getApplicationContext());
        lbl.setText(message);
        lbl.setTextColor(getResources().getColor(R.color.red));
        lbl.setTextSize(12F);
        lbl.setPadding(20, 5, 20, 5);

        button.setPadding(20, 5, 20, 5);

        table.addView(lbl, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
        table.addView(button, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
    }

    private View createTextView(final String user)
    {
        final TextView text = new TextView(this);
        text.setText(user);
        hash.put(getHashKey(user, TEXTVIEW), text);
        text.setTextColor(getResources().getColor(R.color.gray));
        text.setOnLongClickListener(new View.OnLongClickListener() {

			@Override
			public boolean onLongClick(final View v)
			{
				final AlertDialog.Builder builder = new AlertDialog.Builder(MultipleAccountsActivity.this);
				builder.setMessage(getString(R.string.areyousure_removenumber, user))
				.setCancelable(false)
				.setPositiveButton(R.string.removeit, new DialogInterface.OnClickListener() {
					public void onClick(final DialogInterface dialog, final int id) {
						removePhoneNumber(user);

						if (model.isEmpty())
						{
							showAddAccountDialog(getUsersTelephoneNumber(), null);
						}
					}
				})
				.setNegativeButton(R.string.keepit, new DialogInterface.OnClickListener() {
					public void onClick(final DialogInterface dialog, final int id) {
						dialog.cancel();
					}
				});
				final AlertDialog alert = builder.create();

				alert.show();
				return true;
			}
		});

        return text;
    }

	private Button createSignInButton(final UsernamePassword auth)
    {
    	final Button signIn = new Button(getApplicationContext());
    	signIn.setText(R.string.login);
    	signIn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View v)
			{
				showAddAccountDialog(auth.user, auth.pass);
			}
		});

    	return signIn;
    }

	private void removePhoneNumber(final String user)
	{
		final SharedPreferences prefs = getPreferences(MODE_PRIVATE);
		final Editor editor = prefs.edit();
		editor.remove(getPrefUserKey(user));
		editor.remove(getPrefPassKey(user));
		editor.commit();

		updateModelFromPreferences();
	}

    private boolean doesModelContainPhoneNumber(final String number)
	{
    	for (final UsernamePassword auth : model)
    	{
    		if (digits(auth.user).equals(number))
    		{
    			return true;
    		}
    	}
		return false;
	}

    private String getHashKey(final String user, final String type)
    {
        return digits(user) + "|" + type;
    }

    private String getNumberFromHashKey(final String hashKey)
    {
    	// This is kind of a hack implementation, but so long as the "type" doesn't contain
    	// numbers, this will return the 9 digit phone number
    	return digits(hashKey);
    }

    private String digits(final String user)
    {
    	final String ret = user.replaceAll("\\D", "");
    	if (ret.length() == 10)
    	{
    		return ret;
    	}
    	if (ret.length() > 10)
    	{
    		return ret.substring(ret.length() - 10, ret.length() - 1);
    	}
    	// how did they get a shorter than 10 digit phone number?
        return ret;
    }

    private void updatePreferences(final UsernamePassword acct)
    {
		final SharedPreferences prefs = getPreferences(MODE_PRIVATE);
		final Editor editor = prefs.edit();
        editor.putString(getPrefUserKey(acct), acct.user);
        editor.putString(getPrefPassKey(acct), acct.pass);
        editor.commit();

    	updateModelFromPreferences();
    }

    private static final String USER_PREFIX = "USER";
    private static final String PASS_PREFIX = "PASS";

    private boolean isUserPref(final String key)
    {
    	if (key.startsWith(USER_PREFIX))
    	{
    		return true;
    	}
    	else
    	{
    		return false;
    	}
    }

    private boolean isPasswordPref(final String key)
    {
    	if (key.startsWith(PASS_PREFIX))
    	{
    		return true;
    	}
    	else
    	{
    		return false;
    	}
    }

	private String getPrefUserKey(final UsernamePassword acct)
	{
		return getPrefUserKey(acct.user);
	}

    protected String getPrefUserKey(final String phoneNumber)
	{
		return getPrefKey(USER_PREFIX, phoneNumber);
	}

	private String getPrefPassKey(final UsernamePassword acct)
	{
		return getPrefPassKey(acct.user);
	}

	private String getPrefPassKey(final String phoneNumber)
	{
		return getPrefKey(PASS_PREFIX, phoneNumber);
	}

	private String getPrefKey(final String prefix, final String phoneNumber)
	{
		return prefix + digits(phoneNumber);
	}
}
