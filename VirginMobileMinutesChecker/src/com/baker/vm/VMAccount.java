package com.baker.vm;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.jaygoel.virginminuteschecker.IVMCScraper;

/**
 * @author baker
 *
 */
public final class VMAccount
{

	private static final Pattern DATE_PAT = Pattern.compile("(\\d\\d)/(\\d\\d)/(\\d\\d)");
    private static final Pattern MINUTES_PAT = Pattern.compile("(\\d+)\\s*/\\s*(\\d+)");

    public static VMAccount createInvalid(final UsernamePassword iAuth)
    {
        return new VMAccount(iAuth);
    }

    public static VMAccount createEmulatorAccount()
    {
        final VMAccount ret = new VMAccount(new UsernamePassword("5555215554", "password"));

        ret.monthlyCharge = "$40.00";
        ret.balance = "$0.00";
        ret.minAmountDue = "$0.00";
        ret.dueDate = "05/15/11";
        ret.chargedOn = "05/15/11";
        ret.minutesUsed = "400 / 1200";
        ret.dataUsed = "345.0";
        ret.dataTotal = "2560.0";
        ret.isValid = true;

        return ret;
    }

    public static VMAccount createTest(final UsernamePassword auth)
    {
    	final VMAccount ret = new VMAccount(auth);

    	ret.monthlyCharge = "$40.00";
    	ret.balance = "$0.00";
    	ret.minAmountDue = "$0.00";
    	ret.dueDate = "04/25/11";
    	ret.chargedOn = "04/25/11";
    	ret.minutesUsed = "650 / 1200";
    	ret.dataUsed = "345.0";
        ret.dataTotal = "2560.0";
    	ret.isValid = true;

    	return ret;
    }

    public static VMAccount createTest()
    {
    	final VMAccount ret = new VMAccount(new UsernamePassword("5555555555", "test"));

    	ret.monthlyCharge = "$40.00";
    	ret.balance = "$0.00";
    	ret.minAmountDue = "$0.00";
    	ret.dueDate = "04/31/11";
    	ret.chargedOn = "04/31/11";
    	ret.minutesUsed = "400 / 1200";
    	ret.dataUsed = "345.0";
        ret.dataTotal = "2560.0";
    	ret.isValid = true;

    	return ret;
    }

    public static VMAccount createFromCache(final UsernamePassword iAuth,
    										final String iMinutes,
    										final String iChargedOn)
    {
    	final VMAccount account = new VMAccount(iAuth);

    	account.chargedOn = iChargedOn;
    	account.minutesUsed = iMinutes;

    	return account;
    }

    public VMAccount(final UsernamePassword iAuth, final String html, final IVMCScraper scraper)
    {
    	auth = iAuth;
        isValid = scraper.isValid(html);
        if (isValid)
        {
            number = scraper.getPhoneNumber(html);
            monthlyCharge = scraper.getMonthlyCharge(html);
            balance = scraper.getCurrentBalance(html);
            minAmountDue = scraper.getMinAmountDue(html);
            dueDate = scraper.getDateDue(html);
            chargedOn = scraper.getChargedOn(html);
            minutesUsed = scraper.getMinutesUsed(html);
            dataUsed = scraper.getDataUsed(html);
            dataTotal = scraper.getDataTotal(html);
        }
        else
        {
            number = null;
            monthlyCharge = null;
            balance = null;
            minAmountDue = null;
            dueDate = null;
            chargedOn = null;
            minutesUsed = null;
            dataUsed = null;
            dataTotal = null;
        }
    }

    private VMAccount(final UsernamePassword iAuth)
    {
    	auth = iAuth;
        isValid = false;
        number = auth.user;
        monthlyCharge = null;
        balance = null;
        minAmountDue = null;
        dueDate = null;
        chargedOn = null;
        minutesUsed = null;
        dataUsed = null;
        dataTotal = null;
    }

    private final UsernamePassword auth;
    private boolean isValid;
    private String number;
    private String monthlyCharge;
    private String balance;
    private String minAmountDue;
    private String dueDate;
    private String chargedOn;
    private String minutesUsed;
    private String dataUsed;
    private String dataTotal;

    public boolean isValid()
    {
        return isValid;
    }

    public String getNumber()
    {
        return number;
    }
    public String getMonthlyCharge()
    {
        return monthlyCharge;
    }
    public String getBalance()
    {
        return balance;
    }
    public String getMinAmountDue()
    {
        return minAmountDue;
    }
    public String getDueDate()
    {
        return dueDate;
    }
    public String getChargedOn()
    {
        return chargedOn;
    }
    public boolean canParseChargedOnDate()
    {
    	return DATE_PAT.matcher(getChargedOn() == null ? "" : getChargedOn()).matches();
    }
    public Calendar getChargedOnCal()
    {
    	final Matcher m = DATE_PAT.matcher(getChargedOn());
    	Calendar cal = null;
    	if (m.matches())
    	{
    		// This won't throw NumberFormatExceptions because the matches must be digits
    		cal = new GregorianCalendar(Integer.parseInt("20" + m.group(3)),
    									Integer.parseInt(m.group(1)) - 1,
    									Integer.parseInt(m.group(2)),
    									23,
    									59);
    	}

    	return cal;
    }
    public String getMinutesUsed()
    {
        return minutesUsed;
    }
    public boolean canParseMinutes()
    {
    	return MINUTES_PAT.matcher(getMinutesUsed() == null ? "" : getMinutesUsed()).matches();
    }
    public int getMinutesUsedInt()
    {
    	int used = -1;
    	final Matcher m = MINUTES_PAT.matcher(getMinutesUsed());
    	if (m.matches())
    	{
    		used = Integer.parseInt(m.group(1));
    	}
    	return used;
    }
    public int getMinutesTotal()
    {
    	int total = -1;
    	final Matcher m = MINUTES_PAT.matcher(getMinutesUsed());
    	if (m.matches())
    	{
    		total = Integer.parseInt(m.group(2));
    	}
    	return total;
    }
    
    public String getDataUsed()
    {
    	return dataUsed;
    }
    
    public String getDataTotal()
    {
    	return dataTotal;
    }

	public UsernamePassword getAuth()
	{
		return auth;
	}

}
