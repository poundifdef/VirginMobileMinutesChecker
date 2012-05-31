package com.jaygoel.virginminuteschecker;

/**
 * Interface for VirginMobileChecker's Website Scraper
 */
public interface IVMCScraper {
    boolean isValid(String str);
    String getPhoneNumber(String str);
    String getMonthlyCharge(String str);
    String getCurrentBalance(String str);
    String getMinAmountDue(String str);
    String getDateDue(String str);
    String getChargedOn(String str);
    String getMinutesUsed(String str);
    String getDataUsed(String str);
    String getDataTotal(String str);
}