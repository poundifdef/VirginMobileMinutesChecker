package com.vm.MockAccount;

import com.vm.accountdata.AccountData;
import com.vm.accountdata.PhoneCall;
import com.vm.accountdata.TextMessage;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;

public class MockAccountData implements AccountData 
{
   // TODO: add setters to make this mock class mutable

    public MockAccountData() {
        // TODO: allow you to specify fields in constructor
    }

    public String getPhoneNumber() {
        return "123-456-7890";
    }

    public String getPassword() {
        return "31415";
    }

    public int getTotalMonthlyMinutes() {
        return 1337;
    }

    public int getMonthlyMinutesUsed() {
        return 271;
    }

    public Date getBillDueDate() {
        return new Date();
    }

    public double getAccountBalance() {
        return 11.23;
    }

    public double getMonthlyCharge() {
        return 58.13;
    }

    public int getDataUsed() {
        return 1011;
    }

    public int getDatasCap() {
        return 4096;
    }

    public List<TextMessage> getTextMessageHistory(Date startDate, Date endDate) {
        List<TextMessage> messages = new ArrayList<TextMessage>();
        messages.add(new MockTextMessage());
        messages.add(new MockTextMessage());
        messages.add(new MockTextMessage());

        return messages;
    }

    public List<PhoneCall> getPhoneCallHistory(Date startDate, Date endDate) {
        List<PhoneCall> phoneCalls = new ArrayList<PhoneCall>();
        phoneCalls.add(new MockPhoneCall());

        return phoneCalls;
    }
}
