package de.procilon.oss.yamp.api.shared;

import de.procilon.oss.yamp.serialization.Message;

public interface Encodable
{
    byte[] encode();
    
    default String type()
    {
        return getClass().getName();
    }
    
    default int version()
    {
        return 1;
    }
    
    default Message toMessage()
    {
        return new Message( version(), type(), encode() );
    }
}
