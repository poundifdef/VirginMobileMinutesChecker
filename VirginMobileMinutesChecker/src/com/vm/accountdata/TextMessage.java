package com.vm.accountdata;

import java.util.Date;

public interface TextMessage
{
    public Date getMessageTime();

    // TODO: Should this return something more than a string, which
    //       has whatever is necessary to link with android contacts?
    public String getMessageFrom(); // could be an email address or phone number?

    public boolean wasSent(); // true if message was 'sent', false if 'received'

    // For convenience. Opposite of wasSent().
    // Implementation could be 'return !wasSent();'
    public boolean wasReceived(); 
}
