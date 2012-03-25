package com.vm.accountdata;

import java.util.Date;

public interface PhoneCall
{
    public Date getCallTime();
    public int getCallDuration(); // minutes
    public String getMinutesUsed();

    // If someone went over their minutes, how much did this call cost them?
    public double getCost();
}
