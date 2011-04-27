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

	public String string = "unset";

	public MinutesGraphDrawable(final VMAccount account)
	{
		super();

		updateModel(account);
	}

	protected void updateModel(final VMAccount account)
	{
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
			start.set(Calendar.MONTH, end.get(Calendar.MONTH) - 1);
			final Calendar now = new GregorianCalendar();

			final long total = end.getTimeInMillis() - start.getTimeInMillis();
			final long millis = now.getTimeInMillis() - start.getTimeInMillis();

			string = toString(end);
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
		return end.get(Calendar.MONTH) + "/" + end.get(Calendar.DAY_OF_MONTH) + "/" + end.get(Calendar.YEAR);
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
}
