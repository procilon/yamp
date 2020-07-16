package de.procilon.oss.yamp.api.shared;

import de.procilon.oss.yamp.serialization.Message;

public interface Encodable
{
    int INITIAL_VERSION = 1;
    
    byte[] encode();
    
    default String type()
    {
        return getClass().getName();
    }
    
    default int version()
    {
        return INITIAL_VERSION;
    }
    
    default Message toMessage()
    {
        return new Message( version(), type(), encode() );
    }
}
