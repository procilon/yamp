package de.procilon.oss.yamp.transport.ws;

import java.nio.ByteBuffer;

import de.procilon.oss.yamp.api.processor.YampDispatcher;

/**
 * Utility functions for YAMP WebSocket transport handling.
 * 
 * @author fichtelmannm
 *
 */
public class YampWebsockets
{
    private YampWebsockets()
    {}
    
    /**
     * process the message with the provided dispatcher, but echo a prepended request id back in front of the response.
     * 
     * @param dispatcher
     *            the {@link YampDispatcher} that performs message processing
     * @param message
     *            the inbound message with prepended request id (8 bytes)
     * @return the response-message with the requestId prepended.
     */
    public static ByteBuffer processWebsocketMessage( YampDispatcher dispatcher, ByteBuffer message )
    {
        long requestId = message.getLong();
        
        ByteBuffer response = dispatcher.process( message );
        
        return ByteBuffer.allocate( Long.BYTES + response.remaining() )
                .putLong( requestId )
                .put( response )
                .flip();
    }
}
