package com.vm.MockAccount;

import com.vm.accountdata.PhoneCall;
import java.util.Date;

public class MockPhoneCall implements PhoneCall
{
    public MockPhoneCall() {
        // TODO: make this useful
    }

    public Date getCallTime() {
        return new Date();
    }

    public int getCallDuration() { // minutes
        return 69;
    }

    public int getMinutesUsed() {
        // Possibly these values could be different? Website has two diff columns.
        return getCallDuration();
    }

    // If someone went over their minutes, how much did this call cost them?
    public double getCost() {
        return 0.42;
    }
}
