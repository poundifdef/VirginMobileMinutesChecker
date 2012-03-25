package com.vm.accountdata;

import java.util.Date;

public interface TextMessage
{
    public Date getMessageTime();
    public int getMessageFrom(); // could be an email address or phone number?
    public boolean wasSent(); // true if message was 'sent', false if 'received'

    // For convenience. Opposite of wasSent().
    // Implementation could be 'return !wasSent();'
    public boolean wasReceived(); 
}
