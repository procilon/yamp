package de.procilon.oss.yamp.serialization;

import static java.nio.ByteBuffer.wrap;
import static java.nio.charset.StandardCharsets.US_ASCII;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.nio.ByteBuffer;

import org.junit.jupiter.api.Test;

import de.procilon.oss.yamp.RelayedException;

class ResponseContainerTest
{
    @Test
    void encodeDecodeSuccess()
    {
        ByteBuffer response = wrap( "Test response".getBytes( US_ASCII ) );
        
        ResponseContainer responseContainer = ResponseContainer.ofSuccess( response.slice() );
        
        ByteBuffer encoded = responseContainer.encode();
        ResponseContainer decoded = ResponseContainer.decode( encoded );
        
        assertFalse( decoded.isError() );
        assertEquals( decoded.getMessage(), response );
    }
    
    @Test
    void encodeDecodeError()
    {
        String message = "test error";
        Throwable error = new UnsupportedOperationException( message );
        
        ResponseContainer responseContainer = ResponseContainer.ofError( error );
        
        ByteBuffer encoded = responseContainer.encode();
        
        RelayedException thrown = assertThrows( RelayedException.class, () -> ResponseContainer.decode( encoded ) );
        
        assertThat( thrown.getType(), is( UnsupportedOperationException.class.getName() ) );
        assertThat( thrown.getOriginalMessage(), is( message ) );
    }
}
