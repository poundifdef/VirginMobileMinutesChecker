package com.vm.accountdata;

import java.util.Date;
import java.util.List;

public interface AccountData 
{
    public String getPhoneNumber();
    public String getPassword();

    public int getTotalMonthlyMinutes();
    public int getMonthlyMinutesUsed();

    public Date getBillDueDate();

    public double getAccountBalance();
    public double getMonthlyCharge();

    public int getDataUsed(); // Units for this field should be bytes
    public int getDatasCap();

    public List<TextMessage> getTextMessageHistory(Date startDate, Date endDate);
    public List<PhoneCall> getPhoneCallHistory(Date startDate, Date endDate);
}
