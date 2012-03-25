package com.vm.accountdata;

import java.util.Date;

public interface PhoneCall
{
    public Date getCallTime();

    // TODO: Should this return something more than a string, which
    //       has whatever is necessary to link with android contacts?
    public String getCallFrom(); // could be an email address or phone number?

    public int getCallDuration(); // minutes
    public int getMinutesUsed();

    // If someone went over their minutes, how much did this call cost them?
    public double getCost();
}
