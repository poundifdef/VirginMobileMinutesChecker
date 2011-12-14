/**
 *
 */
package com.baker.vm.ui;

import java.util.Calendar;
import java.util.GregorianCalendar;

import android.graphics.drawable.ShapeDrawable;

import com.baker.vm.VMAccount;

/**
 * @author baker
 *
 */
public abstract class MinutesGraphDrawable extends ShapeDrawable
{


	private boolean hasMinutes;
	private boolean hasDates;

	private float minutesPercent;
	private float datePercent;

	private VMAccount account;

	public String string = "unset";

	public MinutesGraphDrawable(final VMAccount iAccount)
	{
		super();

		updateModel(iAccount);
	}

	protected void updateModel(final VMAccount iAccount)
	{
		account = iAccount;

		if (account != null && account.canParseMinutes())
		{
			minutesPercent =
				account.getMinutesUsedInt() / (float) account.getMinutesTotal();
			hasMinutes = true;
		}
		else
		{
			minutesPercent = -1;
			hasMinutes = false;
		}

		if (account != null && account.canParseChargedOnDate())
		{
			final Calendar end = account.getChargedOnCal();
			final Calendar start = (Calendar) end.clone();
			final Calendar now = new GregorianCalendar();
			
            start.set(Calendar.HOUR_OF_DAY, 0);
            start.set(Calendar.MINUTE, 0);
            start.add(Calendar.MONTH, -1);
           
            now.set(Calendar.HOUR_OF_DAY, 0);
            now.set(Calendar.MINUTE, 0);
            
			if(now.compareTo(start) == -1) {
				// it is prior to the "month start" (30 days before account is 
				// charged), so the user has already paid for the following month.
				//
				// it isn't useful to tell them none of their minutes have been used
				// in a month that hasn't even started yet, so move everything back
				// a month into the actual month we are in.
				
				end.add(Calendar.MONTH, -1);
				start.add(Calendar.MONTH, -1);
			}
			
			final long total = end.getTimeInMillis() - start.getTimeInMillis();
			final long millis = now.getTimeInMillis() - start.getTimeInMillis();

			string = toString(end);
			
			// on the day of the actual charge the account should be
			// at 100 percent progress.
			//
			// this is regardless of when the month end is calculated
			// to be.
			
			if(now.get(Calendar.DATE) == start.get(Calendar.DATE) &&
					now.get(Calendar.MONTH) == start.get(Calendar.MONTH))
				datePercent = 1F;
			else
				datePercent = millis / (float) total;
			
			hasDates = true;
		}
		else
		{
			datePercent = -1;
			hasDates = false;
		}
	}

	private String toString(final Calendar end)
	{
		return end.get(Calendar.MONTH) + "/" + end.get(Calendar.DAY_OF_MONTH) + "/" + end.get(Calendar.YEAR) + " " + end.get(Calendar.HOUR_OF_DAY) + ":" + end.get(Calendar.MINUTE);
	}

	/**
	 * @return true if the minutes value is valid
	 */
	protected final boolean hasMinutes()
	{
		return hasMinutes;
	}

	/**
	 * @return the percent of minutes the account has used
	 */
	protected final float getMinutesPercent()
	{
		return minutesPercent;
	}

	/**
	 * @return true if the dates value is valid
	 */
	protected final boolean hasDates()
	{
		return hasDates;
	}

	/**
	 * @return the percent of time that has elapsed during this billing period
	 */
	protected final float getDatePercent()
	{
		return datePercent;
	}

	/**
	 * @return the account that populated the percent or null
	 */
	protected final VMAccount getAccount()
	{
		return account;
	}
}
