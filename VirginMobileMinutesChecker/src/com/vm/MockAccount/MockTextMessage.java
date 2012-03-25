package com.vm.MockAccount;

import com.vm.accountdata.TextMessage;
import java.util.Date;

public class MockTextMessage implements TextMessage
{
    public Date getMessageTime() {
        return new Date();
    }

    public String getMessageFrom() {
        return "555-112-3581";
    }

    public boolean wasSent() {
        return true;
    }

    public boolean wasReceived(){
        return !wasSent();
    }
}
