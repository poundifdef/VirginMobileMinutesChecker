/**
 *
 */
package com.baker.vm.ui;

import java.util.ArrayList;
import java.util.List;

import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;

import com.baker.vm.ScraperUtil;
import com.baker.vm.UsernamePassword;
import com.baker.vm.VMAccount;
import com.jaygoel.virginminuteschecker.IVMCScraper;
import com.jaygoel.virginminuteschecker.R;
import com.jaygoel.virginminuteschecker.ReferenceScraper;

/**
 * @author baker
 *
 */
public class FetchAccountTask
	extends AsyncTask<UsernamePassword, VMAccount, List<VMAccount>>
{

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
            VMAccount acct = ScraperUtil.scrape(a, scraper);

        	if (acct != null)
        	{
                accts.add(acct);
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
