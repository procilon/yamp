package de.procilon.oss.yamp.serialization;

import de.procilon.oss.yamp.api.shared.Response;

/**
 * A Yamp {@link Response} that represents empty data. The encoded form is a 0-length binary.
 * 
 * @author fichtelmannm
 *
 */
public class VoidResponse implements Response
{
    /**
     * static instance to avoid overhead.
     */
    public static final VoidResponse INSTANCE = new VoidResponse();
    
    private VoidResponse()
    {}
    
    @Override
    public byte[] encode()
    {
        return new byte[0];
    }
    
    /**
     * Create a {@link VoidResponse} based on the message. Fails if the message contains response data.
     * 
     * @param message
     *            the message to decode.
     * @return the VoidResponse {@link #INSTANCE}
     * @throws IllegalArgumentException
     *             if message contains non-empty data
     */
    public static VoidResponse decode( Message message )
    {
        if ( message.getData().length > 0 )
        {
            throw new IllegalArgumentException( "non-empty data is not Void" );
        }
        
        return INSTANCE;
    }
}