package com.jaygoel.virginminuteschecker.tests;

import junit.framework.TestCase;
import com.jaygoel.virginminuteschecker.WebsiteScraper;
import com.jaygoel.virginminuteschecker.IVMCScraper;
import com.jaygoel.virginminuteschecker.ReferenceScraper;
import java.util.Map;

public class WebsiteScraperTests extends TestCase {
    String simpleData;

    protected void setUp() throws Exception {
	super.setUp();
	simpleData= "<p class=\"tel\">TEL</p>\n" 
	    + "<h3>Monthly Charge</h3><p>CHARGE</p>\n"
	    + "<h3>Current Balance</h3><p>BALANCE</p>\n"
	    + "<h3>Min. Amount Due</h3><p>MINAMOUNTDUE</p>\n"
	    + "<h3>Date Due</h3><p>DATEDUE</p>\n"
	    + "<h3>You will be charged on</h3><p>CHARGEDON</p>\n"
	    + "<p id=\"remaining_minutes\"><strong>MINUTES</strong></p>\n"
	    ;
    }

    public void testOldScraper_Simple() {
	Map<String, String> rc= WebsiteScraper.parseInfo(simpleData);

	assertEquals("TRUE", rc.get("isValid"));
	assertEquals("TEL", rc.get("Phone Number"));
	assertEquals("CHARGE", rc.get("Monthly Charge"));
	assertEquals("BALANCE", rc.get("Current Balance"));
	assertEquals("MINAMOUNTDUE", rc.get("Amount Due"));
	assertEquals("DATEDUE", rc.get("Date Due"));
	assertEquals("CHARGEDON", rc.get("Charged on"));
	assertEquals("MINUTES", rc.get("Minutes Used"));
    }

    public void testReferenceScraper_Simple() {
	IVMCScraper RS= new ReferenceScraper();

	assertEquals(true, RS.isValid(simpleData));
	assertEquals("TEL", RS.getPhoneNumber(simpleData));
	assertEquals("CHARGE", RS.getMonthlyCharge(simpleData));
	assertEquals("BALANCE", RS.getCurrentBalance(simpleData));
	assertEquals("MINAMOUNTDUE", RS.getMinAmountDue(simpleData));
	assertEquals("DATEDUE", RS.getDateDue(simpleData));
	assertEquals("CHARGEDON", RS.getChargedOn(simpleData));
	assertEquals("MINUTES", RS.getMinutesUsed(simpleData));
    }

}