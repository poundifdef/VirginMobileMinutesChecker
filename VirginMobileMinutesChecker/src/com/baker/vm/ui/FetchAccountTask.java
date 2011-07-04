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
import com.baker.vm.widget.WidgetUtil;
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

        activity.findViewById(R.id.progresslayout).setVisibility(View.GONE);
    }

    @Override
    protected void onPreExecute()
    {
        super.onPreExecute();

        activity.findViewById(R.id.progresslayout).setVisibility(View.VISIBLE);
    }

    @Override
    protected void onPostExecute(final List<VMAccount> result)
    {
        super.onPostExecute(result);

        activity.updateLayout(result);
        activity.findViewById(R.id.progresslayout).setVisibility(View.GONE);
        
        WidgetUtil.updateAllWidgets(activity.getApplicationContext());
    }

    @Override
    protected void onProgressUpdate(final VMAccount... values)
    {
        super.onProgressUpdate(values);

        final ProgressBar bar = (ProgressBar) activity.findViewById(R.id.progress);
        bar.setProgress(i);
        activity.findViewById(R.id.progresslayout).setVisibility(View.VISIBLE);

        if (values != null && values.length > 0)
        {
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
        publishProgress();

        final IVMCScraper scraper= new ReferenceScraper();

        final List<VMAccount> accts = new ArrayList<VMAccount>();
        for (final UsernamePassword a : params)
        {
            final VMAccount acct = ScraperUtil.scrape(a, scraper);

            ++i;
            if (acct != null)
            {
            	accts.add(acct);
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
