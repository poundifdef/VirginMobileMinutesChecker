/**
 *
 */
package com.baker.vm.ui;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup.LayoutParams;
import android.widget.TextView;

import com.baker.vm.PreferencesUtil;
import com.baker.vm.UsernamePassword;
import com.baker.vm.VMAccount;
import com.jaygoel.virginminuteschecker.R;

/**
 * @author baker
 *
 */
public final class MultipleAccountsActivity extends Activity
{

    private static final Pattern PHONE_NUMBER_PAT =
        Pattern.compile("(\\d{3})?(\\d{3})(\\d{4})");

    private static final String TEXTVIEW = "textview";
    private static final String TABLE = "table";
    private static final String LAYOUT = "linearlayout";
    private static final String GRAPH = "graph";

    public static String digits(final String user)
    {
        if (user == null)
        {
            return "";
        }
        final String ret = user.replaceAll("\\D", "");
        if (ret.length() == 10)
        {
            return ret;
        }
        if (ret.length() > 10)
        {
            return ret.substring(ret.length() - 10, ret.length());
        }
        // how did they get a shorter than 10 digit phone number?
        return ret;
    }

    private final List<UsernamePassword> model = new ArrayList<UsernamePassword>();
    private final Hashtable<String, View> hash = new Hashtable<String, View>();

    public MultipleAccountsActivity()
    {

    }

    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.multipleaccounts);

        // get all stored phone numbers / passwords
        // (don't do the layout, that'll happen on resume)
        updateModelFromPreferences(false);

        if (!model.isEmpty())
        {
            // fetch data on create, instead of onResume where every time you
            // enter the app it goes off again.
            new FetchAccountTask(this).execute(model.toArray(new UsernamePassword[0]));
        }
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

				String startingNumber = null;
				if (!PreferencesUtil.
				        containsNumber(this, getUsersTelephoneNumber()))
				{
                    startingNumber = getUsersTelephoneNumber();
				}
				showAddAccountDialog(startingNumber, null);

				return true;

			case R.id.menu_about:

			    startActivity(new Intent(this, AboutActivity.class));

			    return true;

			default:
				return super.onOptionsItemSelected(item);
		}

	}

    public void updateLayout(final UsernamePassword auth)
    {
        final TextView phoneNumber =
            (TextView) hash.get(getHashKey(auth.user, TEXTVIEW));

        if (phoneNumber != null)
        {
            phoneNumber.setText(formatPhoneNumber(auth.user));
            phoneNumber.setTextColor(getResources().getColor(R.color.gray3));
        }

        final LinearLayout table =
            (LinearLayout) hash.get(getHashKey(auth.user, TABLE));

        if (table != null)
        {
            table.removeAllViews();

            if (auth.pass == null || auth.pass.length() == 0)
            {
                addRow(table, createSignInButton(auth));
            }
            else
            {
                final int widest = getMaxWidth(R.string.currentBalance,
                                         R.string.minutesUsed,
                                         R.string.chargedOn,
                                         R.string.monthlyCharge);

                String balance = "";
                String minutes = "";
                String dueDate = "";
                if (getUsersTelephoneNumber().equals(auth.user))
                {
                    balance = PreferencesUtil.getBalance(this);
                    minutes = PreferencesUtil.getMinutesString(this);
                    dueDate = PreferencesUtil.getDueDate(this);
                }
                addRow(table, R.string.currentBalance, balance, widest, true);
                addRow(table, R.string.minutesUsed, minutes, widest, true);
                addRow(table, R.string.chargedOn, dueDate, widest, true);
                addRow(table, R.string.monthlyCharge, "", widest, true);
            }
        }
    }

    public void updateLayout(final List<VMAccount> accounts)
    {
        if (accounts != null)
        {
            for (final VMAccount acct : accounts)
            {
                updateLayout(acct);
            }
        }
    }

    public void updateLayout(final VMAccount acct)
    {
        final TextView phoneNumber =
            (TextView) hash.get(getHashKey(acct.getNumber(), TEXTVIEW));

        if (phoneNumber != null)
        {
            phoneNumber.setText(acct.getNumber());
            phoneNumber.setTextColor(getResources().getColor(R.color.white));
        }

        final LinearLayout table =
            (LinearLayout) hash.get(getHashKey(acct.getNumber(), TABLE));

        if (table != null)
        {
            table.removeAllViews();

            if (acct.isValid())
            {
                final int widest = getMaxWidth(R.string.currentBalance,
                                         R.string.minutesUsed,
                                         R.string.chargedOn,
                                         R.string.monthlyCharge);
                addRow(table, R.string.currentBalance, acct.getBalance(), widest, false);
                addRow(table, R.string.minutesUsed, acct.getMinutesUsed(), widest, false);
                addRow(table, R.string.chargedOn, acct.getChargedOn(), widest, false);
                addRow(table, R.string.monthlyCharge, acct.getMonthlyCharge(), widest, false);

                PreferencesUtil.setCache(this, acct);
            }
            else
            {
                addRow(table, createSignInButton(acct.getAuth()), getString(R.string.loginFail));
            }
        }

        /*
        final MinutesGraphDrawable graph =
        	(MinutesGraphDrawable) hash.get(getHashKey(acct.getNumber(), GRAPH));

        if (graph != null)
        {
        	if (acct.isValid())
        	{
        		graph.updateModel(acct);
        		graph.setVisibility(View.VISIBLE);
        	}
        	else
        	{
        		graph.setVisibility(View.GONE);
        	}
        }

        final LinearLayout layout =
        	(LinearLayout) hash.get(getHashKey(acct.getNumber(), LAYOUT));

        if (graph != null)
        {
        	if (acct.isValid())
        	{
        		layout.setBackgroundDrawable(new MinutesPieGraphDrawable(this, acct));
        	}
        }
        */

    }

	private void removeAllPasswordsFromPreferences()
	{
        final List<String> keys = new ArrayList<String>();
        final SharedPreferences prefs = PreferencesUtil.get(this);
        final Map<String, ?> map = prefs.getAll();

        for (final String key : map.keySet())
        {
        	if (PreferencesUtil.isPasswordPref(key))
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
		return PreferencesUtil.getDefaultTelephoneNumber(getApplicationContext());
	}

	private void updateModelFromPreferences()
	{
	    updateModelFromPreferences(true);
	}

	private void updateModelFromPreferences(final boolean doLayout)
	{
        model.clear();
        final SharedPreferences prefs = PreferencesUtil.get(this);

        final List<String> phoneNumbers = new ArrayList<String>();
        final Map<String, ?> map = prefs.getAll();

        for (final String key : map.keySet())
        {
        	if (PreferencesUtil.isUserPref(key))
        	{
        		phoneNumbers.add(map.get(key).toString());
        	}
        }

        for (final String phoneNumber : phoneNumbers)
        {
        	model.add(new UsernamePassword(phoneNumber,
        	    PreferencesUtil.getPassword(this, phoneNumber)));
        }

        if (doLayout)
        {
            doInitialLayout();
        }
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
                final String phoneNumber = digits(((EditText) dialog.findViewById(R.id.phoneNumberInputView)).getText().toString());
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
        	hash.remove(getHashKey(number, GRAPH));
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

                final LinearLayout container = new LinearLayout(getApplicationContext());
                container.setOrientation(LinearLayout.HORIZONTAL);

                final LinearLayout table = new LinearLayout(getApplicationContext());
                table.setOrientation(LinearLayout.VERTICAL);
                table.setPadding(20, 0, 0, 0);
                hash.put(getHashKey(auth.user, TABLE), table);

                updateLayout(auth);

//                container.addView(table, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1F));

//                final MinutesPieGraph piegraph = new MinutesPieGraph(getApplicationContext());
//                hash.put(getHashKey(auth.user, GRAPH), piegraph);

//                container.addView(piegraph, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1F));

                vert.addView(table);

                v.addView(vert);
        	}
        }
    }

    private void addRow(final LinearLayout table,
    					final int labelResId,
    					final String value,
    					final int width,
    					final boolean isFromCache)
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
        lbl.setTextColor(getResources().getColor(R.color.gray3));
        lbl.setTextSize(12F);
        lbl.setMinimumWidth(width + 5);

        final TextView val = new TextView(this);
        val.setText(value);
        final int colorResId = isFromCache ? R.color.gray3 : R.color.white;
        val.setTextColor(getResources().getColor(colorResId));

        final LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.addView(lbl, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        row.addView(val, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1F));

        table.addView(row, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
    }

    private void addRow(final LinearLayout table,
    					final View button)
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
        lbl.setTextColor(getResources().getColor(R.color.error));
        lbl.setTextSize(12F);
        lbl.setPadding(20, 5, 20, 5);

        button.setPadding(20, 5, 20, 5);

        table.addView(lbl, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
        table.addView(button, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
    }

    private View createTextView(final String user)
    {
        final TextView text = new TextView(this);
        text.setText(formatPhoneNumber(user));
        hash.put(getHashKey(user, TEXTVIEW), text);
        text.setTextColor(getResources().getColor(R.color.gray3));
        text.setOnLongClickListener(new View.OnLongClickListener() {

			@Override
			public boolean onLongClick(final View v)
			{
				final AlertDialog.Builder builder = new AlertDialog.Builder(MultipleAccountsActivity.this);
				builder.setMessage(getString(R.string.areyousure_removenumber, user))
				.setCancelable(false)
				.setPositiveButton(R.string.removeit, new DialogInterface.OnClickListener() {
					@Override
                    public void onClick(final DialogInterface dialog, final int id) {
						removePhoneNumber(user);

						if (model.isEmpty())
						{
							showAddAccountDialog(getUsersTelephoneNumber(), null);
						}
					}
				})
				.setNegativeButton(R.string.keepit, new DialogInterface.OnClickListener() {
					@Override
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
	    PreferencesUtil.removeNumber(this, user);

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

    private int getMaxWidth(final int... stringResIds)
    {
        int max = 0;
        final Paint p = new Paint();
        for (final int stringResId : stringResIds)
        {
            max = Math.max(max, (int) p.measureText(getString(stringResId)));
        }
        return max;
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

    private String formatPhoneNumber(final String number)
    {
        String ret = number;
        final Matcher m = PHONE_NUMBER_PAT.matcher(number);
        if (m.matches())
        {
            ret = "(" + m.group(1) + ") " + m.group(2) + "-" + m.group(3);
        }
        return ret;
    }

    private void updatePreferences(final UsernamePassword acct)
    {
        PreferencesUtil.setAuth(this, acct);

    	updateModelFromPreferences();
    }
}
