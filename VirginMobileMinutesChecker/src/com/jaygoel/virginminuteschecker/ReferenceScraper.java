package com.jaygoel.virginminuteschecker;

public class ReferenceScraper implements IVMCScraper {

    /* usage note: don't call any other method if the page data is invalid */
    public boolean isValid(String str) {
	if (str.indexOf("<p class=\"tel\">") < 0)
	    return false;
	return true;
    }
    
    public String getPhoneNumber(String str) {
	String srch= "<p class=\"tel\">";
	int start = str.indexOf(srch);
	int end = str.indexOf("</p>", start);
	return str.substring(start + srch.length(), end);
    }

    public String getMonthlyCharge(String str) {
	String srch= "<h3>Monthly Charge</h3><p>";
	int start = str.indexOf(srch);
	int end = str.indexOf("</p>", start);
	return str.substring(start + srch.length(), end);
    }

    public String getCurrentBalance(String str) {
	String srch= "<h3>Current Balance</h3><p>";
	int start = str.indexOf(srch);
	int end = str.indexOf("</p>", start);
	return str.substring(start + srch.length(), end);
    }

    public String getMinAmountDue(String str) {
	String srch= "<h3>Min. Amount Due</h3><p>";
	int start = str.indexOf(srch);
	int end = str.indexOf("</p>", start);
   	    
	if ((start > 0) && (end > 0)) {
	    return str.substring(start + srch.length(), end);
	} else {
	    // throw error?
	    return null;
	}
    }

    public String getDateDue(String str) {
	String srch = "<h3>Date Due</h3><p>";
	int start = str.indexOf(srch);
	int end = str.indexOf("</p>", start);
   	    
	if ((start > 0) && (end > 0)) {
	    return str.substring(start + srch.length(), end);
	} else {
	    // throw error?
	    return null;
	}
    }

    public String getChargedOn(String str) {
	String srch = "<h3>You will be charged on</h3><p>";
	int start = str.indexOf(srch);
	int end = str.indexOf("</p>", start);
   	    
	if ((start > 0) && (end > 0)) {
	    return str.substring(start + srch.length(), end);
	} else {
	    // throw error?
	    return null;
	}
    }

    public String getMinutesUsed(String str) {
	String srch = "<p id=\"remaining_minutes\"><strong>";
	int start = str.indexOf(srch);
	int end = str.indexOf("</p>", start);
   	    
	return str.substring(start + srch.length(), end).replaceFirst("</strong>", "");
    }
    
}