/**
 *
 */
package com.baker.vm;

import java.util.ArrayList;
import java.util.List;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.jaygoel.virginminuteschecker.IVMCScraper;
import com.jaygoel.virginminuteschecker.R;
import com.jaygoel.virginminuteschecker.ReferenceScraper;
import com.jaygoel.virginminuteschecker.WebsiteScraper;

/**
 * @author baker
 *
 */
public class FetchAccountTask
	extends AsyncTask<UsernamePassword, VMAccount, List<VMAccount>>
{

	private static final String TAG = "FetchAccountTask";

	private final MultipleAccountsActivity activity;
	private int i = 0;

	public FetchAccountTask(final MultipleAccountsActivity iActivity)
	{
		activity = iActivity;
	}

    @Override
    protected void onCancelled()
    {
        super.onCancelled();

        activity.findViewById(R.id.progress).setVisibility(View.GONE);
    }

    @Override
    protected void onPreExecute()
    {
        super.onPreExecute();

        activity.findViewById(R.id.progress).setVisibility(View.VISIBLE);
    }

    @Override
    protected void onPostExecute(final List<VMAccount> result)
    {
        super.onPostExecute(result);

        activity.updateLayout(result);
        activity.findViewById(R.id.progress).setVisibility(View.GONE);
    }

    @Override
    protected void onProgressUpdate(final VMAccount... values)
    {
        super.onProgressUpdate(values);

        if (values != null && values.length > 0)
        {
            final ProgressBar bar = (ProgressBar) activity.findViewById(R.id.progress);
            bar.setProgress(i);

            for (final VMAccount acct : values)
            {
            	activity.updateLayout(acct);
            }
        }
    }

    @Override
    protected List<VMAccount> doInBackground(final UsernamePassword... params)
    {
        ((ProgressBar) activity.findViewById(R.id.progress)).setMax(params.length);

        final IVMCScraper scraper= new ReferenceScraper();

        final List<VMAccount> accts = new ArrayList<VMAccount>();
        for (final UsernamePassword a : params)
        {
            VMAccount acct = null;

        	if (a.pass != null && a.pass.length() != 0)
        	{
                final String html= WebsiteScraper.fetchScreen(a.user, a.pass);
                Log.d(TAG, html);

                if (scraper.isValid(html))
                {
                    Log.d(TAG, "valid");
                    acct = new VMAccount(a, html, scraper);
                    accts.add(acct);
                }
                else
                {
                    Log.d(TAG, "invalid: " + a.toString());
                    acct = VMAccount.createInvalid(a);
                    accts.add(acct);
                }
        	}

            ++i;
            if (acct != null)
            {
            	publishProgress(acct);
            }
            else
            {
            	publishProgress();
            }
        }
        return accts;
    }
}
