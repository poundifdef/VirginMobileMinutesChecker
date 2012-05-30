package com.jaygoel.virginminuteschecker;

public class ReferenceScraper implements IVMCScraper
{

    /* usage note: don't call any other method if the page data is invalid */
    @Override
    public boolean isValid(final String str)
    {
        if (str.indexOf("<p class=\"tel\">") < 0)
        {
            return false;
        }
        return true;
    }

    @Override
    public String getPhoneNumber(final String str)
    {
        String srch = "<p class=\"tel\">";
        int start = str.indexOf(srch);
        int end = str.indexOf("</p>", start);
        return str.substring(start + srch.length(), end);
    }

    @Override
    public String getMonthlyCharge(final String str)
    {
        String srch = "<h3>Next Month's Charge</h3><p>";
        int start = str.indexOf(srch);
        int end = str.indexOf("</p>", start);
        return str.substring(start + srch.length(), end);
    }

    @Override
    public String getCurrentBalance(final String str)
    {
        String srch = "<h3>Current Balance</h3><p>";
        int start = str.indexOf(srch);
        int end = str.indexOf("</p>", start);
        return str.substring(start + srch.length(), end);
    }

    @Override
    public String getMinAmountDue(final String str)
    {
        String srch = "<h3>Min. Amount Due</h3><p>";
        int start = str.indexOf(srch);
        int end = str.indexOf("</p>", start);

        if ((start > 0) && (end > 0))
        {
            return str.substring(start + srch.length(), end);
        }
        else
        {
            // throw error?
            return null;
        }
    }

    @Override
    public String getDateDue(final String str)
    {
        String srch = "<h3>Date Due</h3><p>";
        int start = str.indexOf(srch);
        int end = str.indexOf("</p>", start);

        if ((start > 0) && (end > 0))
        {
            return str.substring(start + srch.length(), end);
        }
        else
        {
            // throw error?
            return null;
        }
    }

    @Override
    public String getChargedOn(final String str)
    {
        String srch = "<h3>You will be charged on</h3><p>";
        int start = str.indexOf(srch);
        int end = str.indexOf("</p>", start);

        if ((start > 0) && (end > 0))
        {
            return str.substring(start + srch.length(), end);
        }
        else
        {
            // throw error?
            return null;
        }
    }

    @Override
    public String getMinutesUsed(final String str)
    {
        String srch = "<p id=\"remaining_minutes\"><strong>";
        int start = str.indexOf(srch);
        int end = str.indexOf("</p>", start);

        return str.substring(start + srch.length(), end).replaceFirst(
            "</strong>", "");
    }
    
    @Override
    public String getDataUsed(final String str)
    {
    	String srch = "MB Used: ";
    	int start = str.indexOf(srch);
    	int end = str.indexOf(" MB", start);
    	
    	if((start > 0) && (end > 0))
    	{
    		return str.substring(start + srch.length(), end);
    	}
    	else
    	{
    		return null;
    	}
    }
    
    @Override
    public String getDataTotal(final String str)
    {
    	String srch = "Data speeds may be reduced at ";
    	int start = str.indexOf(srch);
    	int end = str.indexOf(" MB", start);
    	
    	if((start > 0) && (end > 0))
    	{
    		return str.substring(start + srch.length(), end);
    	}
    	else
    	{
    		return null;
    	}
    }

}
