/**
 *
 */
package com.baker.vm;

import android.util.Log;

import com.jaygoel.virginminuteschecker.IVMCScraper;
import com.jaygoel.virginminuteschecker.ReferenceScraper;
import com.jaygoel.virginminuteschecker.WebsiteScraper;

/**
 * @author baker
 *
 */
public final class ScraperUtil
{
    private static final String TAG = "ScraperUtil";

    private ScraperUtil()
    {

    }

    public static VMAccount scrape(final UsernamePassword a)
    {
        return scrape(a, new ReferenceScraper());
    }

    public static VMAccount scrape(final UsernamePassword a,
                                   final IVMCScraper scraper)
    {
        VMAccount acct = null;

        if (a.pass != null && a.pass.length() != 0)
        {
            try
            {
                final String html= WebsiteScraper.fetchScreen(a.user, a.pass);
                Log.d(TAG, html);

                if (scraper.isValid(html))
                {
                    Log.d(TAG, "valid");
                    acct = new VMAccount(a, html, scraper);
                }
                else
                {
                    Log.d(TAG, "invalid: " + a.toString());
                    acct = VMAccount.createInvalid(a);
                }
            }
            catch (Exception ex)
            {
                Log.e(TAG, "Failed to fetch virgin mobile info: " + a.user);
            }
        }

        return acct;
    }
}
