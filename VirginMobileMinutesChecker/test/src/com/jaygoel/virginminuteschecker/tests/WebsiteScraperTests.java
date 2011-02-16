package com.jaygoel.virginminuteschecker.tests;

import junit.framework.TestCase;
import com.jaygoel.virginminuteschecker.WebsiteScraper;
import java.util.Map;

public class WebsiteScraperTests extends TestCase {
    String testData;

    protected void setUp() throws Exception {
	super.setUp();
	testData= "<p class=\"tel\">TEL</p>\n" 
	    + "<h3>Monthly Charge</h3><p>CHARGE</p>\n"
	    + "<h3>Current Balance</h3><p>BALANCE</p>\n"
	    + "<h3>Min. Amount Due</h3><p>MINAMOUNTDUE</p>\n"
	    + "<h3>Date Due</h3><p>DATEDUE</p>\n"
	    + "<h3>You will be charged on</h3><p>CHARGEDON</p>\n"
	    + "<p id=\"remaining_minutes\"><strong>MINUTES</strong></p>\n"
	    ;
    }

    public void testOldScraper() {
	Map<String, String> rc= WebsiteScraper.parseInfo(testData);

	assertEquals("TRUE", rc.get("isValid"));
	assertEquals("TEL", rc.get("Phone Number"));
	assertEquals("BALANCE", rc.get("Current Balance"));
	assertEquals("MINAMOUNTDUE", rc.get("Amount Due"));
	assertEquals("DATEDUE", rc.get("Date Due"));
	assertEquals("CHARGEDON", rc.get("Charged on"));
	assertEquals("MINUTES", rc.get("Minutes Used"));
    }

}